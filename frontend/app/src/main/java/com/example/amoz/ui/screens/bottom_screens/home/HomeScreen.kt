package com.example.amoz.ui.screens.bottom_screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.amoz.api.sealed.ResultState
import com.example.amoz.navigation.NavItemType
import com.example.amoz.navigation.bottomNavigationBarNavItemsMap
import com.example.amoz.data.NavItem
import com.example.amoz.models.ProductOrderSummary
import com.example.amoz.ui.components.MoreOrdersTextButton
import com.example.amoz.ui.components.ResultStateView
import com.example.amoz.ui.theme.AmozApplicationTheme
import com.example.amoz.view_models.OrdersViewModel
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun HomeScreen(
    navController: NavController,
    navigateToScreen: (NavItem) -> Unit,
    paddingValues: PaddingValues,
    ordersViewModel: OrdersViewModel,
) {
    val ordersUiState by ordersViewModel.ordersUiState.collectAsState()

    val stateView = ordersUiState.ordersListFetched
    val currency by ordersViewModel.getCurrency().collectAsState(initial = "USD")

    LaunchedEffect(Unit) {
        ordersViewModel.fetchOrdersListOnScreenLoad()
    }
    ResultStateView(
        state = stateView,
        onPullToRefresh = { ordersViewModel.fetchOrdersList(skipLoading = true) }
    ) { ordersList ->

        AmozApplicationTheme {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    CardsLazyRow(cardsList = ordersViewModel.createHomeCardList(ordersUiState))
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                    )
                    MoreOrdersTextButton(
                        onClick = {
                            val ordersNavItem = bottomNavigationBarNavItemsMap[NavItemType.Orders]
                            navigateToScreen(ordersNavItem!!)
                        })
                    LastOrdersList(
                        ordersList = ordersList,
                        ordersViewModel = ordersViewModel,
                        maxListItemsVisible = 10,
                        currency = currency!!
                    )
                }
            }
        }

    }
}



