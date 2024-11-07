package com.home.cryptotop5

import com.home.cryptotop5.data.local.CryptoDao
import com.home.cryptotop5.data.local.CryptoLocalItem
import com.home.cryptotop5.data.remote.CryptoApiItem
import com.home.cryptotop5.data.remote.CryptoApiService
import com.home.cryptotop5.data.repository.CryptoErrorResponseState
import com.home.cryptotop5.data.repository.CryptoRepository
import com.home.cryptotop5.data.repository.CryptoSuccessfulResponseState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class CryptoRepositoryTest {

    private lateinit var repository: CryptoRepository
    private lateinit var apiService: CryptoApiService
    private lateinit var cryptoDao: CryptoDao

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        // Mocking the dependencies
        apiService = mockk()
        cryptoDao = mockk(relaxed = true, relaxUnitFun = true)

        // Creating the repository with mocked dependencies
        repository = CryptoRepository(apiService, cryptoDao)

        // Set up the test dispatcher for coroutines
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // Resetting the dispatcher after the test
        Dispatchers.resetMain()
    }

    @Test
    fun `test getCryptos - cache hit with valid data`() = testScope.runTest {
        // Given: Cached data is valid
        val cachedData = listOf(
            CryptoLocalItem(
                id = "1",
                name = "Bitcoin",
                symbol = "BTC",
                current_price = 40000.0,
                price_change_percentage_24h = 5.0,
                timestamp = Instant.now().toEpochMilli()
            )
        )
        coEvery { cryptoDao.getCryptos() } returns cachedData

        // When: Repository method is called
        val result = repository.getCryptos()

        // Then: We should get a successful response with the cached data
        assertTrue(result is CryptoSuccessfulResponseState)
        val successfulState = result as CryptoSuccessfulResponseState
        assertEquals(1, successfulState.coins.size)
        assertEquals("Bitcoin", successfulState.coins[0].name)
        assertEquals(40000.0, successfulState.coins[0].priceUsd, 0.1)
    }

    @Test
    fun `test getCryptos - network failure`() = testScope.runTest {
        // Given: Cache is empty or expired
        val cachedData = emptyList<CryptoLocalItem>()
        coEvery { cryptoDao.getCryptos() } returns cachedData

        // Mock the network failure (non-successful response)
        val networkResponse = Response.error<List<CryptoApiItem>>(500, "Internal Server Error".toResponseBody())
        coEvery { apiService.getTopCryptoPrices() } returns networkResponse

        // When: Repository method is called
        val result = repository.getCryptos()

        // Then: We should get an error response
        assertTrue(result is CryptoErrorResponseState)
    }

    @Test
    fun `test getCryptos - network response with empty data`() = testScope.runTest {
        // Given: Cache is empty or expired
        val cachedData = emptyList<CryptoLocalItem>()
        coEvery { cryptoDao.getCryptos() } returns cachedData

        // Mock the network response with no data
        val networkResponse = Response.success<List<CryptoApiItem>>(emptyList())
        coEvery { apiService.getTopCryptoPrices() } returns networkResponse

        // When: Repository method is called
        val result = repository.getCryptos()

        // Then: We should get an error response
        assertTrue(result is CryptoErrorResponseState)
    }
}
