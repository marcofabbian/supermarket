package com.phoenix.supermarket.domain

import com.phoenix.supermarket.domain.repository.inMemory.InMemoryProductRepository
import kotlin.test.*

class InMemoryProductRepositoryTest {

    @Test
    fun `basic CRUD operations`() {
        // create with empty initial data to test CRUD behavior from empty state
        val repo = InMemoryProductRepository(initial = emptyList())

        val p1 = Product(1, "img1", "Milk", "SuperMart", "DairyCo", 1.0)
        val p2 = Product(2, "img2", "Bread", "BakeryShop", "BakeHouse", 1.5)

        // empty at start
        assertTrue(repo.all().isEmpty())

        // save
        repo.save(p1)
        repo.save(p2)

        val all = repo.all()
        assertEquals(2, all.size)
        assertEquals(p1, repo.findById(1))
        assertEquals(p2, repo.findById(2))

        // delete
        assertTrue(repo.delete(1))
        assertNull(repo.findById(1))
        assertFalse(repo.delete(999))
    }
}
