package com.example.listadecompras.presentation.shop_item_screen.components

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
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
    ),
    errorText: @Composable (() -> Unit)? = null,
    showError: Boolean,
    label: @Composable (() -> Unit)? = null,
    onTextChange: (String) -> Unit
) {
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            label = label,
            value = text,
            keyboardOptions = keyboardOptions,
            onValueChange = onTextChange,
            supportingText = errorText,
            isError = showError
        )
}

@Preview(showBackground = true)
@Composable
//fun PreviewItemNameInputField(isError : Boolean = true) {
fun PreviewItemNameInputField() {
    val isError = true
    ItemInputField(
        modifier = Modifier.padding(all = 16.dp),
        label = { Text(text = "Name") },
        onTextChange = {},
        text = "",
        errorText = {
            if (isError) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "please enter a name",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        showError = isError
    )
}