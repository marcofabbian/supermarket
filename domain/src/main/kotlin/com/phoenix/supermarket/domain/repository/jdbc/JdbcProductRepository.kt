package com.phoenix.supermarket.domain.repository.jdbc

import com.phoenix.supermarket.domain.Product
import com.phoenix.supermarket.domain.repository.ProductRepository
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

/**
 * JDBC-backed ProductRepository implementation. This class intentionally lives in the domain
 * module so repository implementations are colocated with the domain API.
 *
 * NOTE: this implementation depends on Spring's JdbcTemplate; the domain module declares
 * a compile-time dependency on `spring-jdbc` so the class compiles. The application module
 * (web-api) wires this implementation into Spring when the `db` profile is active.
 */
class JdbcProductRepository(private val jdbc: JdbcTemplate) : ProductRepository {

    private val mapper = RowMapper { rs: ResultSet, _: Int ->
        Product(
            id = rs.getLong("id"),
            image = rs.getString("image_url") ?: "",
            name = rs.getString("name"),
            supermarket = rs.getString("supermarket_name") ?: "",
            brand = rs.getString("brand") ?: "",
            price = rs.getBigDecimal("price").toDouble()
        )
    }

    override fun all(): List<Product> {
        return jdbc.query("SELECT p.id, p.image_url, p.name, p.brand, p.price, s.name as supermarket_name FROM product p LEFT JOIN supermarket s ON p.supermarket_id = s.id ORDER BY p.id", mapper)
    }

    override fun findById(id: Long): Product? {
        val sql = "SELECT p.id, p.image_url, p.name, p.brand, p.price, s.name as supermarket_name FROM product p LEFT JOIN supermarket s ON p.supermarket_id = s.id WHERE p.id = ?"
        return try {
            jdbc.queryForObject(sql, mapper, id)
        } catch (ex: Exception) {
            null
        }
    }

    override fun save(product: Product): Product {
        if (product.id == 0L) {
            val sql = "INSERT INTO product (image_url, name, brand, price) VALUES (?, ?, ?, ?) RETURNING id"
            val id = jdbc.queryForObject(sql, Long::class.java, product.image, product.name, product.brand, product.price)
            return product.copy(id = id ?: product.id)
        } else {
            val sql = "UPDATE product SET image_url = ?, name = ?, brand = ?, price = ? WHERE id = ?"
            jdbc.update(sql, product.image, product.name, product.brand, product.price, product.id)
            return product
        }
    }

    override fun delete(id: Long): Boolean {
        val rows = jdbc.update("DELETE FROM product WHERE id = ?", id)
        return rows > 0
    }

    override fun find(q: String?, page: Int, size: Int): Pair<List<Product>, Int> {
        val params = mutableListOf<Any>()
        val where = if (q.isNullOrBlank()) {
            ""
        } else {
            val like = "%${q.trim().lowercase()}%"
            params.add(like)
            params.add(like)
            params.add(like)
            "WHERE LOWER(p.name) LIKE ? OR LOWER(p.brand) LIKE ? OR LOWER(s.name) LIKE ?"
        }

        val countSql = "SELECT COUNT(*) FROM product p LEFT JOIN supermarket s ON p.supermarket_id = s.id $where"
        val totalObj: Number? = if (params.isEmpty()) {
            jdbc.queryForObject(countSql, Integer::class.java)
        } else {
            jdbc.queryForObject(countSql, params.toTypedArray(), Integer::class.java)
        }
        val total = totalObj?.toInt() ?: 0

        val offset = page * size
        val dataSql = "SELECT p.id, p.image_url, p.name, p.brand, p.price, s.name as supermarket_name FROM product p LEFT JOIN supermarket s ON p.supermarket_id = s.id $where ORDER BY p.id LIMIT ? OFFSET ?"
        params.add(size)
        params.add(offset)

        val items = if (params.isEmpty()) {
            jdbc.query(dataSql, mapper)
        } else {
            jdbc.query(dataSql, mapper, *params.toTypedArray())
        }
        return Pair(items, total)
    }
}