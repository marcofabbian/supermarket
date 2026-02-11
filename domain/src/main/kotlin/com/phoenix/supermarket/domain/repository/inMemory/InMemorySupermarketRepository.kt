package com.phoenix.supermarket.domain.repository.inMemory

import com.phoenix.supermarket.domain.Supermarket
import com.phoenix.supermarket.domain.repository.SupermarketRepository
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicLong

/**
 * Simple in-memory SupermarketRepository useful for tests and when DB isn't available.
 */
class InMemorySupermarketRepository(initial: List<Supermarket> = emptyList()) : SupermarketRepository {

    private val idGen = AtomicLong(1)
    private val store = linkedMapOf<Long, Supermarket>()

    init {
        initial.forEach { s ->
            val id = if (s.id == 0L) idGen.getAndIncrement() else { idGen.set(maxOf(idGen.get(), s.id + 1)); s.id }
            store[id] = s.copy(id = id)
        }
        // ensure there is a default 'supermarket' repo entry
        if (store.values.none { it.name == "supermarket" }) {
            val id = idGen.getAndIncrement()
            store[id] = Supermarket(
                id = id,
                name = "supermarket",
                repoBase = "https://github.com/your-org/supermarket",
                createdAt = OffsetDateTime.now()
            )
        }
    }

    override fun all(): List<Supermarket> = store.values.toList()

    override fun findById(id: Long): Supermarket? = store[id]

    override fun findByName(name: String): Supermarket? = store.values.find { it.name == name }

    override fun save(supermarket: Supermarket): Supermarket {
        return if (supermarket.id == 0L) {
            val id = idGen.getAndIncrement()
            val s = supermarket.copy(id = id)
            store[id] = s
            s
        } else {
            store[supermarket.id] = supermarket
            supermarket
        }
    }

    override fun delete(id: Long): Boolean = store.remove(id) != null
}