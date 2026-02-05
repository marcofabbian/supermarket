package com.phoenix.supermarket.domain

/**
 * Repository abstraction for Product entities. Implementations can be in-memory, JDBC, JPA, etc.
 */
interface ProductRepository {
    /** Return all products */
    fun all(): List<Product>

    /** Find a product by id, or null if not found */
    fun findById(id: Long): Product?

    /** Save or update a product. Returns the saved product. */
    fun save(product: Product): Product

    /** Delete product by id */
    fun delete(id: Long): Boolean

    /**
     * Find products matching an optional query (searches name/brand/supermarket) with pagination.
     * Returns a Pair(items, totalMatchingItems).
     */
    fun find(q: String?, page: Int, size: Int): Pair<List<Product>, Int>
}
