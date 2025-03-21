package com.example.geo_tasknotifier.data.remote

import com.example.geo_tasknotifier.model.GeoResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApi {

    @GET("search")
    suspend fun getCoordinates(
        @Query("q") address: String,
        @Query("api_key") apiKey: String
    ): GeoResponse

    companion object {
        fun getGeoInstance(): GeoApi {
            return Retrofit.Builder()
                .baseUrl("https://geocode.maps.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeoApi::class.java)
        }
    }
}