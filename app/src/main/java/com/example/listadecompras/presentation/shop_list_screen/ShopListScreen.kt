package com.example.listadecompras.presentation.shop_list_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listadecompras.presentation.shop_list_screen.components.ShopItemSwipeable

@Composable
fun ShopListScreen(viewModel: MainComposeViewModel = hiltViewModel()){
    val state = viewModel.state.value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}
            ){}
        }
    ) {padding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ){
            itemsIndexed(
                state.shopList,
                key = { _, item -> item.hashCode() }
            )
            { _, shopItem ->
                ShopItemSwipeable(
                    shopItem = shopItem,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {},
                    onRemove = viewModel::deleteShopItem,
                    onToggle = viewModel::changeEnableState
                )
            }
        }
    }
}

