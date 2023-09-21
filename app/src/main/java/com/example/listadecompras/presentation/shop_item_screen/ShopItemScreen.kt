package com.example.listadecompras.presentation.shop_item_screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.listadecompras.presentation.SingleViewModel

@Composable
fun ShopItemScreen(
    navController: NavController,
    screenMode: String,
    viewModel: SingleViewModel = hiltViewModel()
) {
        when (screenMode) {
            "modeEdit" -> viewModel.saveClick = { viewModel.editShopItem() }
            "modeAdd" -> viewModel.saveClick = { viewModel.addShopItem() }
        }

    Scaffold(Modifier.fillMaxSize()) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
        ) {
            ShopItemEditPane(
                modifier = Modifier.fillMaxSize(),
                itemName = viewModel.shopItemEditName,
                itemCount = viewModel.shopItemEditCount,
                showErrorName = viewModel.showErrorName,
                showErrorCount = viewModel.showErrorCount,
                onNameChange = { name -> viewModel.onNameChanged(name) },
                onCountChange = { count -> viewModel.onCountChanged(count) },
                onClick = {
                    viewModel.onSaveClick()
                    if (viewModel.finish) navController.navigateUp()
                }
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
