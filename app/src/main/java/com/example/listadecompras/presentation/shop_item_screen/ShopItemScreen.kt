package com.example.listadecompras.presentation.shop_item_screen


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.listadecompras.presentation.ShopItemComposeFragment
import com.example.listadecompras.presentation.shop_item_screen.components.FragmentContainer

@Composable
fun ShopItemScreen(
    fragment: ShopItemComposeFragment?,
    fragmentManager: FragmentManager,
) {
    Scaffold(Modifier.fillMaxSize()) { padding ->
        fragment?.let { fragment ->
            FragmentContainer(
                modifier = Modifier.fillMaxSize(),
                //                fragmentManager = supportFragmentManager,
                fragmentManager = fragmentManager,
                commit = {
                    replace(it, fragment)
                    commit()
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
