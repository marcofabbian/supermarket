// NOTE: Kotlin code below — consider renaming this file to ProductService.kt
package com.example.supermarket.service

import com.example.supermarket.annotation.ReadOnlyService

@ReadOnlyService
class ProductService {
    // ...existing code...

    fun getAllProducts(): List<String> {
        // example stub - replace with repository calls or real logic
        return listOf("Apples", "Bananas", "Milk")
    }

    // ...existing code...
}
