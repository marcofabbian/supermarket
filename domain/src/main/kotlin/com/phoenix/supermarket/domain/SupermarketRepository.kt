package com.phoenix.supermarket.domain

interface SupermarketRepository {
    fun all(): List<Supermarket>
    fun findById(id: Long): Supermarket?
    fun findByName(name: String): Supermarket?
    fun save(supermarket: Supermarket): Supermarket
    fun delete(id: Long): Boolean
}

