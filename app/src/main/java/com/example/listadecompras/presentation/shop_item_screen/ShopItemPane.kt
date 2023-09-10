package com.example.listadecompras.presentation.shop_item_screen


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ShopItemEditPane(
    itemName: String,
    showError: Boolean,
    onNameChange: (String) -> Unit,
    onClick: () -> Unit
) {

    Column(modifier = Modifier.padding(16.dp)) {

        ItemNameInputField(

            modifier = Modifier.padding(all = 16.dp),
            label = { Text(text = "Name") },
            name = itemName,
            onNameChange = onNameChange,
            showError = showError
        )

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
        itemName = "",
        showError = false,
        onNameChange = {},
        onClick = {}
    )
}