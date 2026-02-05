package com.phoenix.supermarket.domain

/**
 * Simple in-memory implementation of [ProductRepository]. Useful for tests and simple runtime.
 * This implementation is not thread-safe and intended for demo/dev only.
 */
class InMemoryProductRepository(initial: Collection<Product> = defaultInitial()) : ProductRepository {
    private val map = initial.associateBy { it.id }.toMutableMap()

    override fun all(): List<Product> = map.values.sortedBy { it.id }

    override fun findById(id: Long): Product? = map[id]

    override fun save(product: Product): Product {
        map[product.id] = product
        return product
    }

    override fun delete(id: Long): Boolean = map.remove(id) != null

    override fun find(q: String?, page: Int, size: Int): Pair<List<Product>, Int> {
        val all = all()
        val filtered = if (q.isNullOrBlank()) {
            all
        } else {
            val qs = q.trim().lowercase()
            all.filter { p ->
                p.name.lowercase().contains(qs) || p.brand.lowercase().contains(qs) || p.supermarket.lowercase().contains(qs)
            }
        }

        val total = filtered.size
        if (page < 0 || size <= 0) return Pair(emptyList(), total)
        val start = page * size
        if (start >= total) return Pair(emptyList(), total)
        val end = minOf(start + size, total)
        val pageItems = filtered.subList(start, end)
        return Pair(pageItems, total)
    }

    companion object {
        private fun defaultInitial(): List<Product> {
            val categories = listOf(
                "Milk", "Bread", "Apples", "Banana", "Eggs", "Cheese", "Yogurt", "Orange Juice",
                "Butter", "Chicken Breast", "Pasta", "Rice", "Tomatoes", "Potatoes", "Cereal", "Coffee",
                "Tea", "Sugar", "Salt", "Olive Oil"
            )

            val descriptors = listOf("1L", "500g", "1kg", "Pack of 6", "500ml", "200g", "Large", "Small", "Family Pack", "250g")

            val supermarkets = listOf(
                "SuperMart", "BakeryShop", "FreshFarm", "GreenGrocer", "MarketPlace", "DailyNeeds", "BudgetStore",
                "GourmetFoods", "CityMart", "GoodFoods"
            )

            val brands = listOf(
                "DairyCo", "BakeHouse", "Orchard", "FarmFresh", "NatureBest", "PureFarm", "HouseBrand", "Sunrise", "HappyFoods", "EcoGoods"
            )

            return (1..100).map { i ->
                val category = categories[(i - 1) % categories.size]
                val descriptor = descriptors[(i - 1) % descriptors.size]
                val name = "$category $descriptor #$i"
                val supermarket = supermarkets[(i - 1) % supermarkets.size]
                val brand = brands[(i - 1) % brands.size]
                val price = 0.99 + ((i * 37) % 1000) / 100.0
                Product(i.toLong(), "https://picsum.photos/seed/product$i/64", name, supermarket, brand, price)
            }
        }
    }
}
