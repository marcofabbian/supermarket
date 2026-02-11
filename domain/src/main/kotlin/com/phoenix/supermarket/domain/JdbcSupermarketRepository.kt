package com.phoenix.supermarket.domain

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.OffsetDateTime

class JdbcSupermarketRepository(private val jdbc: JdbcTemplate) : SupermarketRepository {

    private val mapper = RowMapper { rs: ResultSet, _: Int ->
        Supermarket(
            id = rs.getLong("id"),
            name = rs.getString("name"),
            logoUrl = rs.getString("logo_url"),
            repoBase = rs.getString("repo_base"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java)
        )
    }

    override fun all(): List<Supermarket> = jdbc.query("SELECT id, name, logo_url, repo_base, created_at FROM supermarket ORDER BY id", mapper)

    override fun findById(id: Long): Supermarket? = try {
        jdbc.queryForObject("SELECT id, name, logo_url, repo_base, created_at FROM supermarket WHERE id = ?", mapper, id)
    } catch (ex: Exception) {
        null
    }

    override fun findByName(name: String): Supermarket? = try {
        jdbc.queryForObject("SELECT id, name, logo_url, repo_base, created_at FROM supermarket WHERE name = ?", mapper, name)
    } catch (ex: Exception) {
        null
    }

    override fun save(supermarket: Supermarket): Supermarket {
        return if (supermarket.id == 0L) {
            val sql = "INSERT INTO supermarket (name, logo_url, repo_base) VALUES (?, ?, ?) RETURNING id, created_at"
            val row = jdbc.queryForObject(sql, RowMapper { rs, _ ->
                val id = rs.getLong("id")
                val created = rs.getObject("created_at", OffsetDateTime::class.java)
                supermarket.copy(id = id, createdAt = created)
            }, supermarket.name, supermarket.logoUrl, supermarket.repoBase)
            row ?: supermarket
        } else {
            val sql = "UPDATE supermarket SET name = ?, logo_url = ?, repo_base = ? WHERE id = ?"
            jdbc.update(sql, supermarket.name, supermarket.logoUrl, supermarket.repoBase, supermarket.id)
            supermarket
        }
    }

    override fun delete(id: Long): Boolean {
        val rows = jdbc.update("DELETE FROM supermarket WHERE id = ?", id)
        return rows > 0
    }
}

