package com.example.amoz.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryFilledButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    leadingIcon: ImageVector? = null,
    leadingIconDescription: String? = null,
    enabled: Boolean = true,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick,
        enabled = enabled
    ) {
        leadingIcon?.let {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingIconDescription
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PrimaryOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    leadingIcon: ImageVector? = null,
    leadingIconDescription: String? = null,
    enabled: Boolean = true,
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(10.dp),
        onClick = onClick,
        enabled = enabled
    ) {
        leadingIcon?.let {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingIconDescription
            )
            Spacer(modifier = Modifier.width(5.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CloseOutlinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String
) {
    OutlinedButton(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error
        )
    }
}