package com.phoenix.supermarket.domain

import java.time.OffsetDateTime

/**
 * Domain model for a supermarket (maps to the `supermarket` table)
 */
data class Supermarket(
    val id: Long = 0,
    val name: String,
    val logoUrl: String? = null,
    val repoBase: String? = null,
    val url: String? = null,
    val createdAt: OffsetDateTime? = null
)
