package com.phoenix.supermarket.webapi

import com.phoenix.supermarket.domain.InMemoryProductRepository
import com.phoenix.supermarket.domain.JdbcProductRepository
import com.phoenix.supermarket.domain.ProductRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class RepositoryConfig {
    @Bean
    @Profile("!db")
    fun inMemoryProductRepository(): ProductRepository = InMemoryProductRepository()

    @Bean
    @Profile("db")
    fun jdbcProductRepository(jdbcTemplate: JdbcTemplate): ProductRepository = JdbcProductRepository(jdbcTemplate)
}
