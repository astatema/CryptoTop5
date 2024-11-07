package com.home.cryptotop5.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class representing a cryptocurrency item in the local database.
 */
@Entity(tableName = "cryptos")
data class CryptoLocalItem(
    @PrimaryKey val id: String,
    val name: String,
    val symbol: String,
    val current_price: Double,
    val price_change_percentage_24h: Double,
    val timestamp: Long
)
