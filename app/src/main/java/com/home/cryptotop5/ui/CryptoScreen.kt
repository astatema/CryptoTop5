package com.home.cryptotop5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.home.cryptotop5.CryptoItemUiState
import com.home.cryptotop5.R
import com.home.cryptotop5.SuccessfulUiState

/**
 * Main UI screen for displaying cryptocurrency data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoScreen(uiState: SuccessfulUiState) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.crypto_screen_header),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        },
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            uiState.cryptoItems.forEach {
                CryptoItemUi(uiState = it)
            }
        }
    }
}

@Composable
private fun CryptoItemUi(uiState: CryptoItemUiState) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = uiState.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(R.string.crypto_screen_price, uiState.price),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = stringResource(R.string.crypto_screen_price_change, uiState.priceChange),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview
@Composable
fun CryptoItemWithGraphPreview() {
    CryptoScreen(
        uiState = SuccessfulUiState(
            cryptoItems = listOf(
                CryptoItemUiState(
                    name = "Bitcoin",
                    price = "$45000",
                    priceChange = "2.5%"
                ),
                CryptoItemUiState(
                    name = "Ethereum",
                    price = "$3000",
                    priceChange = "-1.2%"
                ),
                CryptoItemUiState(
                    name = "Litecoin",
                    price = "$150",
                    priceChange = "0.8%"
                ),
                CryptoItemUiState(
                    name = "Cardano",
                    price = "$1.2",
                    priceChange = "-0.5%"
                ),
                CryptoItemUiState(
                    name = "XRP",
                    price = "$0.5",
                    priceChange = "0.2%"
                )
            )
        )
    )
}
