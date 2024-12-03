package com.example.amoz.pickers

import androidx.navigation.NavController
import com.example.amoz.data.SavedStateHandleKeys
import com.example.amoz.models.ProductSummary
import com.example.amoz.ui.screens.Screens

class ProductPicker(navController: NavController)
    : BasePicker<ProductSummary>(navController, ProductSummary.serializer()) {

    fun isProductPickerMode(): Boolean {
        return getMode(SavedStateHandleKeys.PRODUCT_PICKER_MODE)
    }

    fun getPickedProduct(): ProductSummary? {
        return getPickedItem(SavedStateHandleKeys.PICKED_PRODUCT_SUMMARY)
    }

    fun navigateToProductScreen() {
        setNavElementsVisibleMode(false)
        setProductPickerMode(true)
        navController.navigate(Screens.Products.route)
    }

    fun pickProduct(productSummary: ProductSummary) {
        pickItem(SavedStateHandleKeys.PICKED_PRODUCT_SUMMARY, productSummary)
        navController.popBackStack()
        setProductPickerMode(mode = false)
        setNavElementsVisibleMode(mode = true)
    }

    private fun setProductPickerMode(mode: Boolean) {
        setMode(SavedStateHandleKeys.PRODUCT_PICKER_MODE, mode)
    }
}