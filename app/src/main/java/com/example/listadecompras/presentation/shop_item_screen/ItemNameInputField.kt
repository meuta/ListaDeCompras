package com.example.listadecompras.presentation.shop_item_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ItemNameInputField(
    modifier: Modifier = Modifier,
    name: String,
    showError: Boolean,
    label: @Composable (() -> Unit)? = null,
    onNameChange: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = label,
            value = name,
            onValueChange = onNameChange,
            isError = showError
        )
        ItemNameErrorText(showError)
    }
}

@Composable
fun ItemNameErrorText(showError: Boolean){
    Text(
        text = if (showError) "Invalid name" else "",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.error,
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewItemNameInputField() {
    ItemNameInputField(
        modifier = Modifier.padding(all = 16.dp),
        label = { Text(text = "Name") },
        onNameChange = {},
        name = "",
        showError = false
    )
}