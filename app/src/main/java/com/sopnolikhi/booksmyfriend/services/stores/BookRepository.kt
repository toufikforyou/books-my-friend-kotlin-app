package com.sopnolikhi.booksmyfriend.services.stores

import android.content.Context
import com.sopnolikhi.booksmyfriend.models.books.Book
import com.sopnolikhi.booksmyfriend.models.books.BookCategory

/**
 * Offline catalogue of public-domain books. Each book ships a sample chapter in `assets/books/`.
 * Swap this for a network source once the backend is ready.
 */
object BookRepository {

    val books: List<Book> = listOf(
        Book(
            id = "pride_and_prejudice",
            title = "Pride and Prejudice",
            author = "Jane Austen",
            category = BookCategory.CLASSICS,
            description = "Elizabeth Bennet is clever, quick to judge and in no hurry to marry. When the proud Mr Darcy arrives in the neighbourhood, first impressions turn out to be very wrong. A witty, warm story about love, family and changing your mind.",
            rating = 4.8f,
            readingMinutes = 3,
            languageTag = "en",
            coverStart = 0xFF8E4D2F.toInt(),
            coverEnd = 0xFF4A2414.toInt(),
        ),
        Book(
            id = "alice_in_wonderland",
            title = "Alice's Adventures in Wonderland",
            author = "Lewis Carroll",
            category = BookCategory.CHILDREN,
            description = "A bored afternoon by the river turns strange when a White Rabbit with a pocket watch hurries past. Alice follows him down the rabbit hole into a world of riddles, tea parties and talking cats.",
            rating = 4.7f,
            readingMinutes = 3,
            languageTag = "en",
            coverStart = 0xFF3F7D74.toInt(),
            coverEnd = 0xFF1E4842.toInt(),
        ),
        Book(
            id = "kabuliwala",
            title = "কাবুলিওয়ালা",
            author = "রবীন্দ্রনাথ ঠাকুর",
            category = BookCategory.BANGLA,
            description = "ছোট্ট মিনি আর কাবুল থেকে আসা ফেরিওয়ালা রহমতের অদ্ভুত বন্ধুত্বের গল্প। বাবা ও মেয়ের ভালোবাসা নিয়ে রবীন্দ্রনাথের অন্যতম প্রিয় ছোটগল্প।",
            rating = 4.9f,
            readingMinutes = 2,
            languageTag = "bn",
            coverStart = 0xFFB5652F.toInt(),
            coverEnd = 0xFF6E3412.toInt(),
        ),
        Book(
            id = "sherlock_holmes",
            title = "The Adventures of Sherlock Holmes",
            author = "Arthur Conan Doyle",
            category = BookCategory.MYSTERY,
            description = "Dr Watson returns to Baker Street and finds his old friend restless for a puzzle. A masked visitor, a royal secret and the one woman who outwitted the world's greatest detective.",
            rating = 4.8f,
            readingMinutes = 3,
            languageTag = "en",
            coverStart = 0xFF34495E.toInt(),
            coverEnd = 0xFF15202B.toInt(),
        ),
        Book(
            id = "jungle_book",
            title = "The Jungle Book",
            author = "Rudyard Kipling",
            category = BookCategory.ADVENTURE,
            description = "A wolf family in the Seeonee hills takes in a fearless human cub they name Mowgli, while the tiger Shere Khan waits in the dark for his chance.",
            rating = 4.6f,
            readingMinutes = 2,
            languageTag = "en",
            coverStart = 0xFF4E7D3A.toInt(),
            coverEnd = 0xFF24401A.toInt(),
        ),
        Book(
            id = "little_women",
            title = "Little Women",
            author = "Louisa May Alcott",
            category = BookCategory.CLASSICS,
            description = "Meg, Jo, Beth and Amy March face a lean Christmas while their father is away at war. Four sisters, four dreams, and one home full of laughter and quarrels.",
            rating = 4.7f,
            readingMinutes = 2,
            languageTag = "en",
            coverStart = 0xFF9C4F6B.toInt(),
            coverEnd = 0xFF55253A.toInt(),
        ),
        Book(
            id = "treasure_island",
            title = "Treasure Island",
            author = "Robert Louis Stevenson",
            category = BookCategory.ADVENTURE,
            description = "Young Jim Hawkins remembers the day an old sea captain with a sabre cut across his cheek came to the Admiral Benbow inn, carrying a sea chest and a secret map.",
            rating = 4.6f,
            readingMinutes = 2,
            languageTag = "en",
            coverStart = 0xFF2F6F8E.toInt(),
            coverEnd = 0xFF133A4D.toInt(),
        ),
        Book(
            id = "secret_garden",
            title = "The Secret Garden",
            author = "Frances Hodgson Burnett",
            category = BookCategory.CHILDREN,
            description = "Lonely, sour-tempered Mary Lennox is sent to live in a gloomy manor on the Yorkshire moors, where a locked garden has been waiting ten years for someone to find its key.",
            rating = 4.7f,
            readingMinutes = 2,
            languageTag = "en",
            coverStart = 0xFF6B8E3F.toInt(),
            coverEnd = 0xFF35501C.toInt(),
        ),
    )

    fun find(id: String?): Book? = books.firstOrNull { it.id == id }

    fun byCategory(category: BookCategory?): List<Book> =
        if (category == null) books else books.filter { it.category == category }

    fun search(query: String, category: BookCategory? = null): List<Book> {
        val q = query.trim()
        return byCategory(category).filter {
            q.isEmpty() || it.title.contains(q, ignoreCase = true) || it.author.contains(q, ignoreCase = true)
        }
    }

    fun loadText(context: Context, book: Book): String =
        context.assets.open(book.assetPath).bufferedReader().use { it.readText() }
}
