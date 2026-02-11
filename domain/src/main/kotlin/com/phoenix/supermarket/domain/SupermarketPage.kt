package com.phoenix.supermarket.domain

import java.time.LocalDate
import java.time.OffsetDateTime

/**
 * Domain model representing a page/repo snapshot for a supermarket.
 * Maps to the `supermarket_page` table.
 */
data class SupermarketPage(
    val id: Long = 0,
    val supermarketId: Long,
    val url: String,
    val dateFrom: LocalDate,
    val dateTo: LocalDate? = null,
    val createdAt: OffsetDateTime? = null
)

