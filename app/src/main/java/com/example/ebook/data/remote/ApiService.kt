package com.example.ebook.data.remote

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Mocked Resource wrapper
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

interface ApiService {
    @GET("api/v1/recommendations")
    suspend fun getRecommendations(): Flow<Resource<List<Any>>> // Replace Any with BookResponse

    @GET("api/v1/user/profile")
    suspend fun getUserProfile(): Flow<Resource<Any>> // Replace Any with UserProfile

    @POST("api/v1/library/shelves")
    suspend fun createShelf(@Query("name") name: String): Flow<Resource<Any>>

    @GET("api/v1/library/shelves")
    suspend fun getShelves(): Flow<Resource<List<Any>>>

    @GET("api/v1/dictionary/define")
    suspend fun defineWord(@Query("query") word: String): Flow<Resource<Any>>
}

