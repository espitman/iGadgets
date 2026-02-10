package com.igadgets.app

import retrofit2.http.GET
import retrofit2.http.Path

data class GanjoorPoemResponse(
    val id: Int,
    val title: String,
    val fullTitle: String? = null,
    val fullUrl: String,
    val verses: List<GanjoorVerse> = emptyList()
) {
    val poetName: String
        get() = fullTitle?.split(" » ")?.firstOrNull() ?: "شاعر ناشناس"
}

data class GanjoorVerse(
    val text: String,
    val vOrder: Int
)

interface PoemApiService {
    @GET("api/ganjoor/poem/random")
    suspend fun getRandomPoem(): GanjoorPoemResponse

    @GET("api/ganjoor/poem/{id}")
    suspend fun getPoemById(@Path("id") id: Int): GanjoorPoemResponse
}
