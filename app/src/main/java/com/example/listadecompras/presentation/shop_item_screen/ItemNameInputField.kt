package com.example.listadecompras.presentation.shop_item_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ItemInputField(
    modifier: Modifier = Modifier,
    text: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    showError: Boolean,
    label: @Composable (() -> Unit)? = null,
    onTextChange: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            label = label,
            value = text,
            keyboardOptions = keyboardOptions,
            onValueChange = onTextChange,
            isError = showError
        )
    }
}

@Composable
fun ItemErrorText(showErrorName: Boolean, showErrorCount: Boolean, ){
    Text(
        text = if (showErrorName) "Invalid name" else if (showErrorCount) "Invalid count" else "",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.error,
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewItemNameInputField() {
    ItemInputField(
        modifier = Modifier.padding(all = 16.dp),
        label = { Text(text = "Name") },
        onTextChange = {},
        text = "",
        showError = false
    )
}