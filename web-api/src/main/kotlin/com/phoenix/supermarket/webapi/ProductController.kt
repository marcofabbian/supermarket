package com.phoenix.supermarket.webapi

import com.phoenix.supermarket.domain.Product
import com.phoenix.supermarket.domain.repository.ProductRepository
import com.phoenix.supermarket.domain.repository.jdbc.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val repo: ProductRepository
) {

    @GetMapping
    fun all(
        @RequestParam("q", required = false) q: String?,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "20") size: Int
    ): ResponseEntity<List<Product>> {
        // basic validation
        if (page < 0 || size <= 0) return ResponseEntity.badRequest().build()

        val (items, total) = repo.find(q, page, size)

        return ResponseEntity.ok()
            .header("X-Total-Count", total.toString())
            .body(items)
    }
}
