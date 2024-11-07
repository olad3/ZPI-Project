package com.example.bussiness.ui.screens.bottom_screens.company.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.bussiness.data.Person
import com.example.bussiness.ui.screens.bottom_screens.company.CompanyScreenViewModel
import com.example.bussiness.ui.theme.AmozApplicationTheme

@Composable
fun B2CCustomerScreen(
    b2cCustomersList: List<Person>,
    companyViewModel: CompanyScreenViewModel,
    callSnackBar: (String, ImageVector?) -> Unit,
    ) {
    AmozApplicationTheme {
        val companyUiState by companyViewModel.companyUiState.collectAsState()

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            // -------------------- B2C Customers --------------------
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(b2cCustomersList.reversed()) { person ->
                    ListItem(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp)),
                        headlineContent = { Text(person.firstName + " " + person.lastName) },
                        supportingContent = { Text(text = person.email) },
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                }
            }
            // -------------------- FAB --------------------
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        companyViewModel.expandAddCustomerBottomSheet(true)
                    },
                    modifier = Modifier
                        .padding(16.dp), // Padding for spacing from screen edges
                    icon = { Icon(Icons.Filled.Add, null) },
                    text = { Text(text = "Add customer") }
                )
            }
        }
        if (companyUiState.addCustomerBottomSheetExpanded) {
            AddB2CCustomerBottomSheet(
                onDismissRequest = {
                    companyViewModel.expandAddCustomerBottomSheet(false)
                },
                callSnackBar = callSnackBar,
                addB2CCustomer = { fN, lN, email, phone ->
                    companyViewModel.addB2CCustomer(fN, lN, email, phone)
                }

            )
        }
    }
}
