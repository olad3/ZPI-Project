package com.example.amoz.ui.screens.bottom_screens.products.add_edit_products_bottom_sheets

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.amoz.R
import com.example.amoz.api.requests.ProductCreateRequest
import com.example.amoz.api.sealed.ResultState
import com.example.amoz.models.CategoryDetails
import com.example.amoz.models.ProductAttribute
import com.example.amoz.models.ProductDetails
import com.example.amoz.navigation.NavItemType
import com.example.amoz.navigation.allApplicationScreensMap
import com.example.amoz.navigation.bottomNavigationBarNavItemsMap
import com.example.amoz.ui.components.CloseOutlinedButton
import com.example.amoz.ui.components.PrimaryFilledButton
import com.example.amoz.ui.components.ResultStateView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductTemplateBottomSheet(
    productDetailsState:  MutableStateFlow<ResultState<ProductCreateRequest>>,
    onSaveProduct: (ProductCreateRequest) -> Unit,
    onComplete: (ProductCreateRequest) -> Unit,
    navController: NavController,
    onDismissRequest: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val sheetState =
        rememberModalBottomSheetState( skipPartiallyExpanded = true,
            confirmValueChange = { newState ->
                newState != SheetValue.Hidden
            }
        )

    val listItemColors = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    )
    val listItemModifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(5.dp))
        .border(
            width = 1.dp,
            brush = SolidColor(MaterialTheme.colorScheme.outline),
            shape = RoundedCornerShape(5.dp)
        )


    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        ResultStateView(productDetailsState) { product ->
            var productState by remember { mutableStateOf(product) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // -------------------- Bottom sheet title --------------------
                Text(
                    text = stringResource(R.string.products_add_product_template),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(10.dp))

                // -------------------- Product basic info --------------------
                ProductNameDescriptionPrice(
                    productName = productState.name,
                    productDescription = productState.description,
                    productPrice = productState.price,
                    onNameChange = { productState = productState.copy(name = it) },
                    onDescriptionChange = { productState = productState.copy(description = it) },
                    onPriceChange = { productState = productState.copy(price = it) },
                )

                // -------------------- Product vendor --------------------
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text(stringResource(R.string.product_vendor)) },
                    value = productState.brand ?: "",
                    onValueChange = {
                        productState = productState.copy(brand = it.takeIf { it.isNotBlank() })
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Verified,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    maxLines = 1,
                    singleLine = true
                )

//             -------------------- Product's category --------------------
                ListItem(
                    modifier = listItemModifier.then(Modifier
                        .clickable {
                            onSaveProduct(productState)
                            navController.navigate(
                                allApplicationScreensMap[NavItemType.Categories]!!.screenRoute
                            )
                        }),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Outlined.Category,
                            contentDescription = null
                        )
                    },
                    overlineContent = { Text(stringResource(R.string.product_category)) },
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.product_category_choose)
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    colors = listItemColors
                )

                // -------------------- Product attributes --------------------
                AttributesList(
                    productAttributes = productState.productAttributes,
                    onAttributesChange = {
                        productState = productState.copy(productAttributes = it)
                    }
                )

                // -------------------- Complete adding --------------------
                Spacer(modifier = Modifier.height(15.dp))

                PrimaryFilledButton(
                    onClick = {
                        onComplete(productState)
                        onDismissRequest()
                    },
                    text = stringResource(id = R.string.done),
//                enabled = isFormValid
                )
                // -------------------- Close bottom sheet --------------------
                CloseOutlinedButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            onDismissRequest()
                        }
                    },
                    text = stringResource(R.string.close)
                )
            }
        }
    }
}

