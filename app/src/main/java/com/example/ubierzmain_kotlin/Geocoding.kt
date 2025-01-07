package com.example.ubierzmain_kotlin

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class GeocodingResponse(val results: List<Result>)
data class Result(val address_components: List<AddressComponent>)
data class AddressComponent(val long_name: String, val types: List<String>)

interface GeocodingApi {
    @GET("geocode/json")
    suspend fun getCityName(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}