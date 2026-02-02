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
        return listOf(
            Product(1, "https://picsum.photos/seed/milk/64", "Milk 1L", "SuperMart", "DairyCo", 1.99),
            Product(2, "https://picsum.photos/seed/bread/64", "Bread", "BakeryShop", "BakeHouse", 2.49),
            Product(3, "https://picsum.photos/seed/apples/64", "Apples 1kg", "FreshFarm", "Orchard", 3.79)
        )
    }
}
