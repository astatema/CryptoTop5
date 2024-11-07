package com.home.cryptotop5.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Data access object (DAO) for interacting with the local database.
 */
@Dao
interface CryptoDao {

    @Insert
    suspend fun insertCryptos(cryptos: List<CryptoLocalItem>)

    @Query("SELECT * FROM cryptos")
    suspend fun getCryptos(): List<CryptoLocalItem>
}
