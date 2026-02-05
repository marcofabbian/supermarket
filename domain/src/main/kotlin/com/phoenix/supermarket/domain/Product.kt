package com.phoenix.supermarket.domain

// Moved from web-api; keep the data shape here to be shared across modules
data class Product(
    val id: Long,
    val image: String,
    val name: String,
    val supermarket: String,
    val brand: String,
    val price: Double
)
