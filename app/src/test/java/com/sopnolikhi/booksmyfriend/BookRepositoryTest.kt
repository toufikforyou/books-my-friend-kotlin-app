package com.sopnolikhi.booksmyfriend

import com.sopnolikhi.booksmyfriend.models.books.BookCategory
import com.sopnolikhi.booksmyfriend.services.stores.BookRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookRepositoryTest {

    @Test
    fun bookIdsAreUnique() {
        val ids = BookRepository.books.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun searchMatchesTitleAndAuthorIgnoringCase() {
        assertEquals(listOf("sherlock_holmes"), BookRepository.search("SHERLOCK").map { it.id })
        assertEquals(listOf("pride_and_prejudice"), BookRepository.search("austen").map { it.id })
    }

    @Test
    fun searchMatchesBanglaText() {
        assertEquals(listOf("kabuliwala"), BookRepository.search("রবীন্দ্রনাথ").map { it.id })
    }

    @Test
    fun blankSearchReturnsWholeCategory() {
        val children = BookRepository.search("  ", BookCategory.CHILDREN)
        assertTrue(children.isNotEmpty())
        assertTrue(children.all { it.category == BookCategory.CHILDREN })
    }

    @Test
    fun everyCategoryHasAtLeastOneBook() {
        BookCategory.entries.forEach { category ->
            assertTrue("$category is empty", BookRepository.byCategory(category).isNotEmpty())
        }
    }

    @Test
    fun unknownIdIsNotFound() {
        assertEquals(null, BookRepository.find("missing"))
        assertEquals(null, BookRepository.find(null))
    }
}
