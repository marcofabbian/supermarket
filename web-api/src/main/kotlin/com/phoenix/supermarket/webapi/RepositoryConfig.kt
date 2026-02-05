package com.phoenix.supermarket.webapi

import com.phoenix.supermarket.domain.InMemoryProductRepository
import com.phoenix.supermarket.domain.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfig {
    @Bean
    fun productRepository(): ProductRepository = InMemoryProductRepository()
}
