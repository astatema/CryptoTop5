package com.home.cryptotop5.data.remote

import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit service interface for interacting with the cryptocurrency API.
 */
interface CryptoApiService {

    @GET("v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=5&page=1")
    suspend fun getTopCryptoPrices(): Response<List<CryptoApiItem>>
}
