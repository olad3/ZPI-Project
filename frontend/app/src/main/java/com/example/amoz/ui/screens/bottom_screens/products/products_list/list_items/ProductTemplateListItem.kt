package com.example.amoz.ui.screens.bottom_screens.products.products_list.list_items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.amoz.data.AppPreferences
import com.example.amoz.data.ProductTemplate
import com.example.amoz.ui.screens.bottom_screens.products.products_list.list_items.DismissBackground

@Composable
fun ProductTemplateListItem(
    productTemplate: ProductTemplate,
    onClick: () -> Unit,
    onProductTemplateEdit: (ProductTemplate) -> Unit,
    onProductRemove: (ProductTemplate) -> Unit,
    currency: String,
    positionalThreshold: Float = .45f
) {
    var currentFraction by remember { mutableStateOf(0f) }
    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { newValue ->
            when (newValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    if (currentFraction >= positionalThreshold && currentFraction < 1.0f) {
                        onProductRemove(productTemplate)
                        return@rememberSwipeToDismissBoxState false
                    }
                    return@rememberSwipeToDismissBoxState false
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    if (currentFraction >= positionalThreshold && currentFraction < 1.0f) {
                        onProductTemplateEdit(productTemplate)
                        return@rememberSwipeToDismissBoxState false
                    }
                    return@rememberSwipeToDismissBoxState false
                }
                else -> false
            }
        },
        positionalThreshold = { it * positionalThreshold }
    )



    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            currentFraction = swipeState.progress
            DismissBackground(swipeState)
        }
    ) {
        ListItem(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    onClick = onClick,
                ),
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = null
                )
                productTemplate.mainVariantId?.let {
//                            Image(
//                                painter = rememberAsyncImagePainter(productTemplate.imageUrl),
//                                contentDescription = "Product Image",
//                                contentScale = ContentScale.Crop,
//                                modifier = Modifier
//                                    .size(60.dp)
//                                    .clip(RoundedCornerShape(10.dp))
//                            )
                }
            },
            headlineContent = {
                Text(
                    text = productTemplate.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            supportingContent = {
                Text(
                    text = productTemplate.description,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                Text(
                    text = "${productTemplate.basePrice} $currency"
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
        )
    }
}
