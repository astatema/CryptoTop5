package com.home.cryptotop5.di

import android.content.Context
import androidx.room.Room
import com.home.cryptotop5.data.local.CryptoDao
import com.home.cryptotop5.data.local.CryptoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing dependencies related to the local database.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "crypto_database"

    @Provides
    @Singleton
    fun provideCryptoDatabase(@ApplicationContext appContext: Context): CryptoDatabase {
        return Room.databaseBuilder(
            appContext,
            CryptoDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideCryptoDao(database: CryptoDatabase): CryptoDao {
        return database.cryptoDao()
    }
}
