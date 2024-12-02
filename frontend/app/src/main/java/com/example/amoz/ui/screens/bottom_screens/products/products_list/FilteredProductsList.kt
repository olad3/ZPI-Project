package com.example.amoz.ui.screens.bottom_screens.products.products_list

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.amoz.R
import com.example.amoz.api.sealed.ResultState
import com.example.amoz.data.SavedStateHandleKeys
import com.example.amoz.models.ProductSummary
import com.example.amoz.models.ProductVariantDetails
import com.example.amoz.ui.components.EmptyLayout
import com.example.amoz.ui.components.PrimaryFilledButton
import com.example.amoz.ui.components.ResultStateView
import com.example.amoz.ui.components.text_fields.SearchTextField
import com.example.amoz.view_models.ProductsViewModel
import com.example.amoz.ui.screens.bottom_screens.products.products_list.list_items.ProductListItem
import com.example.amoz.ui.screens.bottom_screens.products.products_list.list_items.ProductVariantListItem
import com.example.amoz.view_models.ProductsViewModel.BottomSheetType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json

@Composable
fun FilteredProductsList(
    productPickerMode: Boolean,
    productVariantPickerMode: Boolean,
    navController: NavController,
    currency: String,
    productsViewModel: ProductsViewModel = hiltViewModel()
) {
    val productsUiState by productsViewModel.productUiState.collectAsState()

    val stateView: MutableStateFlow<ResultState<List<Any>>> =
        if (productsUiState.filteredByProduct == null) {
            productsUiState.productsListFetched as MutableStateFlow<ResultState<List<Any>>>
        } else {
            productsUiState.productVariantsListFetched as MutableStateFlow<ResultState<List<Any>>>
        }

    val listState = rememberLazyListState()
    ResultStateView(
        state = stateView,
        onPullToRefresh = {
            if (productsUiState.filteredByProduct != null) {
                productsViewModel.fetchProductVariantsList(
                    productId = productsUiState.filteredByProduct!!.productId,
                    skipLoading = true
                )
            }
            else {
                productsViewModel.fetchProductsList(skipLoading = true)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            state = listState
        ) {
            val showProducts = productsUiState.showProductsList
            val showProductVariants = productsUiState.showProductVariantsList

            val productsList = productsUiState.filteredSortedProductsList
            val variantsList = productsUiState.filteredSortedProductVariantsList

            // -------------------- Filters --------------------
            item {
                SearchTextField(
                    searchQuery = productsUiState.searchQuery,
                    placeholder = stringResource(id = R.string.search_products_placeholder),
                    onSearchQueryChange = { productsViewModel.updateSearchQuery(it) },
                    onMoreFiltersClick = {
                        productsViewModel.expandBottomSheet(BottomSheetType.MORE_FILTERS, true)
                    }
                )
                FilterChips(
                    productTemplateChipValue = productsUiState.filteredByProduct?.name,
                    onProductChipClick = {
                        productsViewModel.showProductVariants(null)
                    },
                    category = productsUiState.filterParams.category,
                    onCategoryClick = productsViewModel::clearCategoryFilter,
                    priceFrom = productsUiState.filterParams.priceFrom,
                    onPriceFromClick = { productsViewModel.clearPriceFilter(true) },
                    priceTo = productsUiState.filterParams.priceTo,
                    onPriceToClick = { productsViewModel.clearPriceFilter(false) }
                )
            }

            // -------------------- Products list --------------------
            if (showProducts) {

                items(productsList, key = { it.productId }) { product ->
                    Box(
                        modifier = Modifier.animateItem()
                    ) {
                        ProductListItem(
                            product = product,
                            onClick = {
                                if (productPickerMode) {
                                    val productSummaryJson = Json.encodeToString(ProductSummary.serializer(), product)
                                    navController.previousBackStackEntry?.savedStateHandle?.set(
                                        SavedStateHandleKeys.SELECTED_PRODUCT_SUMMARY, productSummaryJson
                                    )
                                    navController.popBackStack()
                                }
                                else { productsViewModel.showProductVariants(product) }
                            },
                            onProductRemove = {
                                productsViewModel.updateCurrentProductToDelete(it)
                                productsViewModel.expandBottomSheet(BottomSheetType.DELETE_PRODUCT, true)
                            },
                            onProductEdit = {
                                productsViewModel.updateCurrentAddEditProduct(it)
                                productsViewModel.expandBottomSheet(BottomSheetType.ADD_EDIT_PRODUCT, true)
                            },
                            currency = currency,
                        )
                    }
                }
                if(productsList.isEmpty()) { item {EmptyLayout {}} }
            }

            // -------------------- ProductVariant list --------------------
            if (showProductVariants) {
                items(variantsList, key = { it.productVariantId }) { productVariant ->
                    productsViewModel.getProductVariantPicture(productVariant.productVariantId)

                    val imageState = productsUiState.productVariantImages
                        .collectAsState().value[productVariant.productVariantId]


                    Box(modifier = Modifier.animateItem()) {
                        ProductVariantListItem(
                            productVariant = productVariant,
                            productVariantBitmapImageState = imageState,
                            currency = currency,
                            onClick = {
                                productsUiState.filteredByProduct?.let { filteredByProduct ->
                                    if (productVariantPickerMode) {
                                        productsViewModel.fetchProductVariantDetails(
                                            productVariantId = productVariant.productVariantId,
                                            onSuccessCallback = {
                                                val productVariantDetailsJson = Json.encodeToString(
                                                    ProductVariantDetails.serializer(), it
                                                )
                                                Log.d("PICKED PRODUCT VARIANT", it.toString())
                                                navController.previousBackStackEntry?.savedStateHandle?.set(
                                                    SavedStateHandleKeys.SELECTED_PRODUCT_VARIANT_DETAILS,
                                                    productVariantDetailsJson
                                                )
                                                navController.popBackStack()
                                            }
                                        )
                                    }
                                    else {
                                        productsViewModel.updateCurrentAddEditProductVariant(
                                            productVariant.productVariantId, filteredByProduct.productId
                                        )
                                        productsViewModel.expandBottomSheet(
                                            BottomSheetType.ADD_EDIT_VARIANT, true
                                        )
                                    }
                                }
                            },
                            onDelete = {
                                productsViewModel.updateCurrentProductVariantToDelete(productVariant)
                                productsViewModel.expandBottomSheet(BottomSheetType.DELETE_PRODUCT, true)
                            }
                        )
                    }
                }
                if(variantsList.isEmpty()) { item {EmptyLayout {} } }
            }
            item {
                PrimaryFilledButton(
                    onClick = {
                        if (showProductVariants) {
                            productsUiState.filteredByProduct?.let {
                                productsViewModel.updateCurrentAddEditProductVariant(null, it.productId)
                                productsViewModel.expandBottomSheet(BottomSheetType.ADD_EDIT_VARIANT, true)
                            }
                        }
                        else {
                            productsViewModel.updateCurrentAddEditProduct(null)
                            productsViewModel.expandBottomSheet(BottomSheetType.ADD_EDIT_PRODUCT, true)

                        }
                    },
                    text =
                    if (showProductVariants) stringResource(R.string.add_product_variant)
                    else stringResource(R.string.products_add_product),
                    leadingIcon = Icons.Default.Add
                )
            }
        }
        BackHandler(enabled = productsUiState.filteredByProduct != null) {
            productsViewModel.showProductVariants(null)
        }
    }
}




