package com.phoenix.supermarket.domain

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicLong

class InMemorySupermarketPageRepository(initial: List<SupermarketPage> = emptyList()) : SupermarketPageRepository {

    private val idGen = AtomicLong(1)
    private val store = linkedMapOf<Long, SupermarketPage>()

    init {
        initial.forEach { p ->
            val id = if (p.id == 0L) idGen.getAndIncrement() else { idGen.set(maxOf(idGen.get(), p.id + 1)); p.id }
            store[id] = p.copy(id = id)
        }
    }

    override fun all(): List<SupermarketPage> = store.values.toList()

    override fun findById(id: Long): SupermarketPage? = store[id]

    override fun findBySupermarket(supermarketId: Long): List<SupermarketPage> = store.values.filter { it.supermarketId == supermarketId }.sortedBy { it.dateFrom }

    override fun findBySupermarketAndDate(supermarketId: Long, date: LocalDate): List<SupermarketPage> = store.values.filter {
        it.supermarketId == supermarketId && (it.dateFrom <= date) && (it.dateTo == null || it.dateTo >= date)
    }.sortedBy { it.dateFrom }

    override fun save(page: SupermarketPage): SupermarketPage {
        return if (page.id == 0L) {
            val id = idGen.getAndIncrement()
            val p = page.copy(id = id, createdAt = OffsetDateTime.now())
            store[id] = p
            p
        } else {
            store[page.id] = page
            page
        }
    }

    override fun delete(id: Long): Boolean = store.remove(id) != null
}

