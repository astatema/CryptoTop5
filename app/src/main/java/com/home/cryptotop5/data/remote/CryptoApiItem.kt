package com.home.cryptotop5.data.remote

/**
 * Data class representing a cryptocurrency item from the API.
 */
data class CryptoApiItem(
    val id: String,
    val name: String,
    val symbol: String,
    val current_price: Double,
    val price_change_percentage_24h: Double
)
