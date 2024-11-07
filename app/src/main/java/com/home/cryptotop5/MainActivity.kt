package com.home.cryptotop5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.home.cryptotop5.ui.CryptoScreen
import com.home.cryptotop5.ui.ErrorScreen
import com.home.cryptotop5.ui.LoadingScreen
import com.home.cryptotop5.ui.theme.CryptoTop5Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // This annotation tells Hilt to provide dependencies to this Activity
class MainActivity : ComponentActivity() {

    // ViewModel initialization using ActivityViewModels delegate
    private val viewModel: CryptoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchCryptos()

        enableEdgeToEdge()
        setContent {
            CryptoTop5Theme {
                val uiState: CryptoUiState by viewModel.cryptoUiState.collectAsStateWithLifecycle()

                when (val state: CryptoUiState = uiState) {
                    LoadingUiState -> {
                        LoadingScreen()
                    }
                    is SuccessfulUiState -> {
                        CryptoScreen(uiState = state)
                    }
                    ErrorUiState -> {
                        ErrorScreen(
                            onRetry = { viewModel.fetchCryptos() }
                        )
                    }
                }
            }
        }
    }
}
