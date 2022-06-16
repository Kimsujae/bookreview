package kr.ac.kumoh.s20160250.bookreviewer.api

import kr.ac.kumoh.s20160250.bookreviewer.model.BestSellerDto
import kr.ac.kumoh.s20160250.bookreviewer.model.SearchBookDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {

    @GET("/api/search.api?output=json")
    fun getBooksByName(
        @Query("key") apiKey: String,
        @Query("query") keyword: String
    ): Call<SearchBookDto>
    @GET("/api/bestSeller.api?output=json&categoryId=100")
    fun getBestSellerBooks(
        @Query("key") apiKey: String,
    ):Call<BestSellerDto>


}