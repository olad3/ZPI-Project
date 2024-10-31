package com.example.bussiness.ui.screens.bottom_screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.bussiness.app.NavigationItem
import com.example.bussiness.app.bottomNavigationBarItems
import com.example.bussiness.ui.screens.Screens
import com.example.bussiness.ui.screens.bottom_screens.orders.OrdersViewModel
import com.example.bussiness.ui.theme.AmozApplicationTheme



@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    salesViewModel: OrdersViewModel = viewModel() ) {
    AmozApplicationTheme {
        Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
        ) {
            val salesUiState by salesViewModel.orderUiState.collectAsState()

            Column {
                CardsLazyRow(cardsList = homeCardItemsList)
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
                MoreOrdersTextButton(
                    navigateToScreen = {
                        navController.navigate(bottomNavigationBarItems[2].screen!!)
                    } )
                LastOrdersLazyList(salesList = salesUiState.salesList, maxListItemsVisible = 10 )
            }
        }
    }
}

@Composable
fun MoreOrdersTextButton(navigateToScreen: () -> Unit) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Last orders: ")
        Text("More",
            modifier = Modifier.clickable {
                navigateToScreen()
            },
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline
            )
    }
}
