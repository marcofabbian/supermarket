package com.phoenix.supermarket.domain

import java.time.LocalDate

interface SupermarketPageRepository {
    fun all(): List<SupermarketPage>
    fun findById(id: Long): SupermarketPage?
    fun findBySupermarket(supermarketId: Long): List<SupermarketPage>
    fun findBySupermarketAndDate(supermarketId: Long, date: LocalDate): List<SupermarketPage>
    fun save(page: SupermarketPage): SupermarketPage
    fun delete(id: Long): Boolean
}

