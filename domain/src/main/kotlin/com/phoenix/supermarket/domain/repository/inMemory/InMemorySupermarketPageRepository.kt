package com.phoenix.supermarket.domain.repository.inMemory

import com.phoenix.supermarket.domain.SupermarketPage
import com.phoenix.supermarket.domain.repository.SupermarketPageRepository
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicLong

// Provide a small default dataset used when no initial list is provided
private fun defaultInitial(): List<SupermarketPage> = listOf(
    SupermarketPage(
        id = 1,
        supermarketId = 1,
        url = "https://github.com/your-org/supermarket",
        dateFrom = LocalDate.parse("2026-01-01"),
        dateTo = null,
        createdAt = OffsetDateTime.now()
    ),
    SupermarketPage(
        id = 2,
        supermarketId = 1,
        url = "https://github.com/your-org/supermarket/archive/2025",
        dateFrom = LocalDate.parse("2025-06-01"),
        dateTo = LocalDate.parse("2025-12-31"),
        createdAt = OffsetDateTime.now().minusMonths(8)
    )
)

class InMemorySupermarketPageRepository(initial: List<SupermarketPage> = defaultInitial()) : SupermarketPageRepository {

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
