package com.example.listadecompras.presentation.shop_item_screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
//    val itemPaneState by shopItemViewModel.uiState.collectAsState()

    Scaffold(Modifier.fillMaxSize()) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            ShopItemEditPane(
                itemName = shopItemViewModel.shopItemEditName,
                itemCount = shopItemViewModel.shopItemEditCount,
                showErrorName = shopItemViewModel.showErrorName,
                showErrorCount = shopItemViewModel.showErrorCount,
                onNameChange = { name -> shopItemViewModel.onNameChanged(name) },
                onCountChange = { count -> shopItemViewModel.onCountChanged(count) },
                onClick = { shopItemViewModel.onSaveClick() }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewShopItemScreen(shopItemViewModel: ShopItemComposeViewModel = hiltViewModel()) {
//    ShopItemScreen(shopItemViewModel = ShopItemViewModel(
////        GetShopItemUseCase(ShopListRepositoryImpl(, ShopListMapper())),
////        AddShopItemUseCase(ShopListRepositoryImpl(),
////    EditShopItemUseCase(ShopListRepositoryImpl()
//    )
}
