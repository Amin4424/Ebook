package com.example.ebook.data.local

import androidx.room.*
import com.example.ebook.data.model.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks WHERE bookId = :bookId ORDER BY page ASC")
    fun getBookmarks(bookId: Int): Flow<List<Bookmark>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)

    @Delete
    suspend fun removeBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE bookId = :bookId AND page = :page")
    suspend fun removeBookmarkByPage(bookId: Int, page: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE bookId = :bookId AND page = :page)")
    fun isBookmarked(bookId: Int, page: Int): Flow<Boolean>
}
