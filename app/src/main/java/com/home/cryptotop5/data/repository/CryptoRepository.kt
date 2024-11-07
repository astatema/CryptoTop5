package com.home.cryptotop5.data.repository

import com.home.cryptotop5.data.local.CryptoDao
import com.home.cryptotop5.data.local.CryptoLocalItem
import com.home.cryptotop5.data.remote.CryptoApiItem
import com.home.cryptotop5.data.remote.CryptoApiService
import retrofit2.Response
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Repository for interacting with the cryptocurrency data.
 */
class CryptoRepository(
    private val apiService: CryptoApiService,
    private val cryptoDao: CryptoDao
) {

    suspend fun getCryptos(): CryptoResponseState {
        val cachedData: List<CryptoLocalItem> = cryptoDao.getCryptos()

        // Check if the cached data is still valid
        val validCacheTimeThreshold: Long = Instant.now()
            .minus(1, ChronoUnit.DAYS)
            .toEpochMilli()
        val validCachedData: List<CryptoLocalItem> = cachedData.filter { it.timestamp > validCacheTimeThreshold }

        return if (validCachedData.isNotEmpty()) {
            CryptoSuccessfulResponseState(
                coins = validCachedData.map { it.toCryptoItem() }
            )
        } else {
            // Fetch from network if cache is expired
            val response: Response<List<CryptoApiItem>> = apiService.getTopCryptoPrices()
            if (response.isSuccessful) {
                val coins: List<CryptoApiItem>? = response.body()
                if (!coins.isNullOrEmpty()) {
                    // Cache the result in database
                    cryptoDao.insertCryptos(coins.map { it.toCryptoLocalItem() })

                    CryptoSuccessfulResponseState(
                        coins = coins.map { it.toCryptoItem() }
                    )
                } else {
                    CryptoErrorResponseState
                }
            } else {
                CryptoErrorResponseState
            }
        }
    }

    // Extension functions to map data

    private fun CryptoApiItem.toCryptoItem(): CryptoItem =
        CryptoItem(
            id = id,
            name = name,
            symbol = symbol,
            priceUsd = current_price,
            priceChangePercentage24h = price_change_percentage_24h
        )

    private fun CryptoLocalItem.toCryptoItem(): CryptoItem =
        CryptoItem(
            id = id,
            name = name,
            symbol = symbol,
            priceUsd = current_price,
            priceChangePercentage24h = price_change_percentage_24h
        )

    private fun CryptoApiItem.toCryptoLocalItem(): CryptoLocalItem =
        CryptoLocalItem(
            id = id,
            name = name,
            symbol = symbol,
            current_price = current_price,
            price_change_percentage_24h = price_change_percentage_24h,
            timestamp = Instant.now().toEpochMilli()
        )
}

/**
 * This class represents the response state of a data request (API or local).
 */
sealed class CryptoResponseState

data class CryptoSuccessfulResponseState(
    val coins: List<CryptoItem>
) : CryptoResponseState()

data class CryptoItem(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: Double,
    val priceChangePercentage24h: Double
)

data object CryptoErrorResponseState : CryptoResponseState()
