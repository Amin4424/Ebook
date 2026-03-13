package com.example.ebook.data.repository

import com.example.ebook.data.SampleData
import com.example.ebook.data.local.BookmarkDao
import com.example.ebook.data.local.ReadingProgressDao
import com.example.ebook.data.model.Book
import com.example.ebook.data.model.Bookmark
import com.example.ebook.data.model.Category
import com.example.ebook.data.model.ReadingProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val readingProgressDao: ReadingProgressDao,
    private val bookmarkDao: BookmarkDao
) {

    fun getAllBooks(): List<Book> = SampleData.sampleBooks

    fun getFeaturedBooks(): List<Book> = SampleData.featuredBooks

    fun getCategories(): List<Category> = SampleData.sampleCategories

    fun getBookById(id: Int): Book? = SampleData.sampleBooks.find { it.id == id }

    fun getBooksByCategory(categoryId: Int): List<Book> =
        SampleData.sampleBooks.filter { it.categoryId == categoryId }

    // Reading Progress
    fun getReadingProgress(bookId: Int): Flow<ReadingProgress?> =
        readingProgressDao.getProgress(bookId)

    fun getLastReadProgress(): Flow<ReadingProgress?> =
        readingProgressDao.getLastRead()

    fun getAllReadingProgress(): Flow<List<ReadingProgress>> =
        readingProgressDao.getAllProgress()

    suspend fun saveReadingProgress(progress: ReadingProgress) =
        readingProgressDao.saveProgress(progress)

    // Bookmarks
    fun getBookmarks(bookId: Int): Flow<List<Bookmark>> =
        bookmarkDao.getBookmarks(bookId)

    suspend fun addBookmark(bookmark: Bookmark) =
        bookmarkDao.addBookmark(bookmark)

    suspend fun removeBookmark(bookmark: Bookmark) =
        bookmarkDao.removeBookmark(bookmark)

    fun isBookmarked(bookId: Int, page: Int): Flow<Boolean> =
        bookmarkDao.isBookmarked(bookId, page)
}
