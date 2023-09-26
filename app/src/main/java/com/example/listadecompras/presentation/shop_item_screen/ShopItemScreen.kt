package com.example.listadecompras.presentation.shop_item_screen


import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun ShopItemScreen(
    navController: NavController,
    viewModel: ShopItemViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val state = viewModel.state.value
    when (state.screenMode) {
        "mode_edit" -> viewModel.saveClick = { viewModel.editShopItem() }
        "mode_add" -> viewModel.saveClick = { viewModel.addShopItem() }
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
                    if (viewModel.finish) context.findActivity()?.finish()
                }
            )
        }
    }
}

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Preview(showBackground = true)
@Composable
fun PreviewShopItemScreen(shopItemViewModel: ShopItemViewModel = hiltViewModel()) {
//    ShopItemScreen(shopItemViewModel = ShopItemViewModel(
////        GetShopItemUseCase(ShopListRepositoryImpl(, ShopListMapper())),
////        AddShopItemUseCase(ShopListRepositoryImpl(),
////    EditShopItemUseCase(ShopListRepositoryImpl()
//    )
    ShopItemScreen(navController = rememberNavController(), shopItemViewModel)
}
