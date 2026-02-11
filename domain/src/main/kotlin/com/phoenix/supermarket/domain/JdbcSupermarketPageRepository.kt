package com.phoenix.supermarket.domain

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.time.LocalDate
import java.time.OffsetDateTime

class JdbcSupermarketPageRepository(private val jdbc: JdbcTemplate) : SupermarketPageRepository {

    private val mapper = RowMapper { rs: ResultSet, _: Int ->
        SupermarketPage(
            id = rs.getLong("id"),
            supermarketId = rs.getLong("supermarket_id"),
            url = rs.getString("url"),
            dateFrom = rs.getObject("date_from", LocalDate::class.java),
            dateTo = rs.getObject("date_to", LocalDate::class.java),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java)
        )
    }

    override fun all(): List<SupermarketPage> = jdbc.query("SELECT id, supermarket_id, url, date_from, date_to, created_at FROM supermarket_page ORDER BY id", mapper)

    override fun findById(id: Long): SupermarketPage? = try {
        jdbc.queryForObject("SELECT id, supermarket_id, url, date_from, date_to, created_at FROM supermarket_page WHERE id = ?", mapper, id)
    } catch (ex: Exception) {
        null
    }

    override fun findBySupermarket(supermarketId: Long): List<SupermarketPage> = jdbc.query("SELECT id, supermarket_id, url, date_from, date_to, created_at FROM supermarket_page WHERE supermarket_id = ? ORDER BY date_from", mapper, supermarketId)

    override fun findBySupermarketAndDate(supermarketId: Long, date: LocalDate): List<SupermarketPage> {
        // Find pages where date is within the [date_from, date_to] range (date_to may be null meaning open-ended)
        val sql = "SELECT id, supermarket_id, url, date_from, date_to, created_at FROM supermarket_page WHERE supermarket_id = ? AND date_from <= ? AND (date_to IS NULL OR date_to >= ?) ORDER BY date_from"
        return jdbc.query(sql, mapper, supermarketId, date, date)
    }

    override fun save(page: SupermarketPage): SupermarketPage {
        return if (page.id == 0L) {
            val sql = "INSERT INTO supermarket_page (supermarket_id, url, date_from, date_to) VALUES (?, ?, ?, ?) RETURNING id, created_at"
            val row = jdbc.queryForObject(sql, RowMapper { rs, _ ->
                val id = rs.getLong("id")
                val created = rs.getObject("created_at", OffsetDateTime::class.java)
                page.copy(id = id, createdAt = created)
            }, page.supermarketId, page.url, page.dateFrom, page.dateTo)
            row ?: page
        } else {
            val sql = "UPDATE supermarket_page SET supermarket_id = ?, url = ?, date_from = ?, date_to = ? WHERE id = ?"
            jdbc.update(sql, page.supermarketId, page.url, page.dateFrom, page.dateTo, page.id)
            page
        }
    }

    override fun delete(id: Long): Boolean {
        val rows = jdbc.update("DELETE FROM supermarket_page WHERE id = ?", id)
        return rows > 0
    }
}

