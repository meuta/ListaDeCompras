package com.example.listadecompras.presentation.shop_item_screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ShopItemScreen(shopItemViewModel: ShopItemComposeViewModel = hiltViewModel()) {
    val itemPaneState by shopItemViewModel.uiState.collectAsState()

    Scaffold() { padding ->
        Box(modifier = Modifier.padding(padding)) {
            ShopItemEditPane(
                itemName = shopItemViewModel.shopItemEditName,
                showError = itemPaneState.showError,
                onNameChange = { name -> shopItemViewModel.onNameChanged(name) },
                onClick = { shopItemViewModel.onSaveClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewShopItemScreen() {
//    ShopItemScreen(shopItemViewModel = ShopItemViewModel(
//        GetShopItemUseCase(ShopListRepositoryImpl()),
//        AddShopItemUseCase(),
//    EditShopItemUseCase()))
}
