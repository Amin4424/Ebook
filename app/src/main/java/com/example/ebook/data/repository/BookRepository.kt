package com.example.ebook.data.repository

import com.example.ebook.data.SampleData
import com.example.ebook.data.local.*
import com.example.ebook.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val readingProgressDao: ReadingProgressDao,
    private val bookmarkDao: BookmarkDao,
    private val highlightDao: HighlightDao,
    private val reviewDao: ReviewDao
) {

    fun getAllBooks(): List<Book> = SampleData.sampleBooks
    fun getFeaturedBooks(): List<Book> = SampleData.featuredBooks
    fun getCategories(): List<Category> = SampleData.sampleCategories
    fun getBookById(id: Int): Book? = SampleData.sampleBooks.find { it.id == id }
    fun getBooksByCategory(categoryId: Int): List<Book> = SampleData.sampleBooks.filter { it.categoryId == categoryId }
    fun searchBooks(query: String): List<Book> = SampleData.sampleBooks.filter {
        it.title.contains(query, ignoreCase = true) || it.author.contains(query, ignoreCase = true)
    }

    // Reading Progress
    fun getReadingProgress(bookId: Int): Flow<ReadingProgress?> = readingProgressDao.getProgress(bookId)
    fun getLastReadProgress(): Flow<ReadingProgress?> = readingProgressDao.getLastRead()
    fun getAllReadingProgress(): Flow<List<ReadingProgress>> = readingProgressDao.getAllProgress()
    suspend fun saveReadingProgress(progress: ReadingProgress) = readingProgressDao.saveProgress(progress)

    // Bookmarks
    fun getBookmarks(bookId: Int): Flow<List<Bookmark>> = bookmarkDao.getBookmarks(bookId)
    suspend fun addBookmark(bookmark: Bookmark) = bookmarkDao.addBookmark(bookmark)
    suspend fun removeBookmark(bookmark: Bookmark) = bookmarkDao.removeBookmark(bookmark)
    fun isBookmarked(bookId: Int, page: Int): Flow<Boolean> = bookmarkDao.isBookmarked(bookId, page)

    // Highlights
    fun getHighlights(bookId: Int, page: Int): Flow<List<Highlight>> = highlightDao.getHighlights(bookId, page)
    fun getAllHighlights(bookId: Int): Flow<List<Highlight>> = highlightDao.getAllHighlights(bookId)
    suspend fun addHighlight(highlight: Highlight) = highlightDao.addHighlight(highlight)
    suspend fun removeHighlight(highlight: Highlight) = highlightDao.removeHighlight(highlight)

    // Reviews
    fun getReviews(bookId: Int): Flow<List<Review>> = reviewDao.getReviews(bookId)
    suspend fun addReview(review: Review) = reviewDao.addReview(review)
    suspend fun deleteReview(review: Review) = reviewDao.deleteReview(review)
}
