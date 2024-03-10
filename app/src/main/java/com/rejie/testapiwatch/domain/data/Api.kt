package com.rejie.testapiwatch.domain.data

import com.rejie.testapiwatch.domain.models.MainResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("products")
    suspend fun getProductsList(
        // If another path
        // @Path("name") name: String
        // If we have a query parameter add like below
        //@Query("api_key") apiKey: String
    ): MainResponse

    companion object {
        const val BASE_URL = "https://dummyjson.com/"
    }
}