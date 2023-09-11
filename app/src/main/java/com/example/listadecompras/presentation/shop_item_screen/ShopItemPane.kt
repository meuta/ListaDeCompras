package com.example.listadecompras.presentation.shop_item_screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ShopItemEditPane(
    itemName: String,
    itemCount: String,
    showErrorName: Boolean,
    showErrorCount: Boolean,
    onNameChange: (String) -> Unit,
    onCountChange: (String) -> Unit,
    onClick: () -> Unit
) {

    Column(modifier = Modifier.padding(16.dp)) {

        ItemInputField(

            modifier = Modifier.padding(all = 16.dp),
            label = { Text(text = "Name") },
            text = itemName,
            onTextChange = onNameChange,
            showError = showErrorName
        )
        ItemErrorText(showErrorName = showErrorName, showErrorCount = false)

//        Spacer(modifier = Modifier.height(16.dp))

        ItemInputField(

            modifier = Modifier.padding(all = 16.dp),
            label = { Text(text = "Count") },
            text = itemCount,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            onTextChange = onCountChange,
            showError = showErrorCount
        )
        ItemErrorText(showErrorName = false, showErrorCount = showErrorCount)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick
        )
        {
            Text(text = "SAVE")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewShopItemEditPane() {
    ShopItemEditPane(
        itemName = "pesto",
        itemCount = "1.9",
        showErrorName = false,
        showErrorCount = false,
        onNameChange = {},
        onCountChange = {},
        onClick = {}
    )
}