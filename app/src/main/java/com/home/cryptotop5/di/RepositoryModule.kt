package com.home.cryptotop5.di

import com.home.cryptotop5.data.local.CryptoDao
import com.home.cryptotop5.data.remote.CryptoApiService
import com.home.cryptotop5.data.repository.CryptoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing dependencies related to the repository.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCryptoRepository(
        apiService: CryptoApiService,
        cryptoDao: CryptoDao
    ): CryptoRepository {
        return CryptoRepository(apiService, cryptoDao)
    }
}
