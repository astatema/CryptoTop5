package com.home.cryptotop5

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.home.cryptotop5.data.repository.CryptoErrorResponseState
import com.home.cryptotop5.data.repository.CryptoItem
import com.home.cryptotop5.data.repository.CryptoRepository
import com.home.cryptotop5.data.repository.CryptoResponseState
import com.home.cryptotop5.data.repository.CryptoSuccessfulResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

/**
 * ViewModel that provides cryptocurrency data to the UI, manages business logic, and interacts with the CryptoRepository for data fetching.
 */
@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val repository: CryptoRepository
) : ViewModel() {

    private val _cryptoUiState: MutableStateFlow<CryptoUiState> = MutableStateFlow(LoadingUiState)
    val cryptoUiState: StateFlow<CryptoUiState> = _cryptoUiState.asStateFlow()

    fun fetchCryptos() {
        _cryptoUiState.update { LoadingUiState }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val responseState: CryptoResponseState = repository.getCryptos()

                _cryptoUiState.update { responseState.toCryptoUiState() }
            } catch (e: Exception) {
                _cryptoUiState.update { ErrorUiState }
            }
        }
    }

    // Extension functions to map data

    private fun CryptoResponseState.toCryptoUiState(): CryptoUiState =
        when (this) {
            is CryptoSuccessfulResponseState -> SuccessfulUiState(
                cryptoItems = coins.map { it.toCryptoItemUiState() }
            )
            CryptoErrorResponseState -> ErrorUiState
        }

    private fun CryptoItem.toCryptoItemUiState(): CryptoItemUiState {
        val changeSign: String = when {
            priceChangePercentage24h < 0 -> "- "
            priceChangePercentage24h > 0 -> "+ "
            else -> ""
        }
        return CryptoItemUiState(
            name = "$name ($symbol)",
            price = "$${String.format(Locale.US, "%.2f", priceUsd)}",
            priceChange = "$changeSign${String.format(Locale.US, "%.2f", abs(priceChangePercentage24h))}%"
        )
    }
}

sealed class CryptoUiState

data object LoadingUiState : CryptoUiState()

data class SuccessfulUiState(
    val cryptoItems: List<CryptoItemUiState>
) : CryptoUiState()

data class CryptoItemUiState(
    val name: String,
    val price: String,
    val priceChange: String
)

data object ErrorUiState : CryptoUiState()
