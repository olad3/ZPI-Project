package com.example.amoz.ui.screens.bottom_screens.orders

import androidx.lifecycle.ViewModel
import com.example.amoz.firebase.FirebaseRepository
import com.example.amoz.data.ProductVariant
import com.example.amoz.firebase.SoldProduct
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OrdersViewModel : ViewModel() {
    private val _orderUiState = MutableStateFlow(OrderUIState())
    val orderUiState: StateFlow<OrderUIState> = _orderUiState.asStateFlow()

    init {
        FirebaseRepository.getSales { salesList ->
            updateSalesList(salesList)
        }
//        FirebaseRepository.getProducts { products ->
//            updateProductsList(products)
//        }
    }

    fun updateSalesList(salesList: List<SoldProduct>) {
        _orderUiState.update { currState ->
            currState.copy(salesList = salesList, salesListIsLoading = false)
        }
    }

    fun updateProductsList(productsList: List<ProductVariant>) {
        _orderUiState.update { currState ->
            currState.copy(productsList = productsList)
        }
    }
    fun updateSoldProduct(soldProduct: SoldProduct) {
        FirebaseRepository.upsertSale(soldProduct)
    }

    fun updateAddEditViewState(
        productEditDialogShow: Boolean,
        currentSoldProductInEdit: SoldProduct = SoldProduct() ) {
        updateCurrentSaleProduct(currentSoldProductInEdit)
        updateDialogShow(productEditDialogShow)
    }

    fun updateDialogShow(dialogShow: Boolean) {
        _orderUiState.update { currState ->
            currState.copy(showAddEditSaleDialog = dialogShow)
        }
    }

    fun showFilterBottomSheet(dialogShow: Boolean) {
        _orderUiState.update { currState ->
            currState.copy(showFilterBottomSheet = dialogShow)
        }
    }

    fun updateCurrentSaleProduct(currentSoldProductInEdit: SoldProduct) {
        _orderUiState.update { currState ->
            currState.copy(currentAddEditSaleProduct = currentSoldProductInEdit)
        }
    }

}