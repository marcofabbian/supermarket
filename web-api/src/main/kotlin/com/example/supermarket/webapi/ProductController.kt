package com.example.supermarket.webapi

import com.example.supermarket.domain.Product
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController {

    // For now return a static list; later this will come from the engine module or a DB
    @GetMapping
    fun all(): List<Product> {
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
