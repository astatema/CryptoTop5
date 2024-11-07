package com.home.cryptotop5.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for caching crypto data.
 */
@Database(entities = [CryptoLocalItem::class], version = 1)
abstract class CryptoDatabase : RoomDatabase() {

    abstract fun cryptoDao(): CryptoDao
}
