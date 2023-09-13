package com.example.listadecompras.presentation.shop_list_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listadecompras.presentation.shop_list_screen.components.ShopItemCard

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
            items(state.shopList)
            {shopItem ->
                ShopItemCard(
                    shopItem = shopItem,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {}
                )
            }
        }
    }
}