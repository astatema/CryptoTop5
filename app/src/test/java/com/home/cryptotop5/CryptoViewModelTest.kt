package com.home.cryptotop5

import app.cash.turbine.test
import com.home.cryptotop5.data.repository.CryptoItem
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CryptoViewModelTest {

    private lateinit var viewModel: CryptoViewModel
    private lateinit var repository: CryptoRepository

    // Set up the dispatcher for coroutines in tests
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        // Mock repository
        repository = mockk()
        viewModel = CryptoViewModel(repository)

        // Setting the dispatcher to our test scope
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        // Reset the dispatcher after the test
        Dispatchers.resetMain()
    }

    @Test
    fun `test fetchCryptos - success case`() = testScope.runTest {
        // Given
        val mockResponse = CryptoSuccessfulResponseState(
            coins = listOf(
                CryptoItem(
                    id = "1",
                    name = "Bitcoin",
                    symbol = "BTC",
                    priceUsd = 40000.0,
                    priceChangePercentage24h = 5.0
                )
            )
        )

        coEvery { repository.getCryptos() } returns mockResponse

        // When
        viewModel.fetchCryptos()

        // Then
        viewModel.cryptoUiState.test {
            // Assert the state changes to SuccessfulUiState with the correct crypto data
            val successfulState = awaitItem() as SuccessfulUiState
            assertEquals(1, successfulState.cryptoItems.size)
            assertEquals("Bitcoin (BTC)", successfulState.cryptoItems[0].name)
            assertEquals("$40000.00", successfulState.cryptoItems[0].price)
            assertEquals("+ 5.00%", successfulState.cryptoItems[0].priceChange)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test fetchCryptos - error case`() = testScope.runTest {
        // Given
        coEvery { repository.getCryptos() } throws Exception("Network Error")

        // When
        viewModel.fetchCryptos()

        // Then
        viewModel.cryptoUiState.test {
            // Assert initial state is Loading
            assertEquals(LoadingUiState, awaitItem())

            // Assert the state changes to ErrorUiState
            assertEquals(ErrorUiState, awaitItem())

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `test fetchCryptos - success case v2`() = testScope.runTest {
        // Given
        val mockResponse = CryptoSuccessfulResponseState(
            coins = listOf(
                CryptoItem(
                    id = "1",
                    name = "Ethereum",
                    symbol = "ETC",
                    priceUsd = 3000.0,
                    priceChangePercentage24h = -5.0
                )
            )
        )
        coEvery { repository.getCryptos() } returns mockResponse

        // When
        viewModel.fetchCryptos()

        // Then
        viewModel.cryptoUiState.test {
            assertEquals(LoadingUiState, awaitItem())  // First, loading state

            // Then, successful state with the correct data
            val successfulState = awaitItem() as SuccessfulUiState
            assertEquals(1, successfulState.cryptoItems.size)
            assertEquals("Ethereum (ETC)", successfulState.cryptoItems[0].name)
            assertEquals("$3000.00", successfulState.cryptoItems[0].price)
            assertEquals("- 5.00%", successfulState.cryptoItems[0].priceChange)

            cancelAndConsumeRemainingEvents()
        }
    }
}
