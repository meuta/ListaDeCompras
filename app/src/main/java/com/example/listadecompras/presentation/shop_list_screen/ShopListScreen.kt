package com.example.listadecompras.presentation.shop_list_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listadecompras.presentation.shop_list_screen.components.LazyColumnSwappable

@Composable
fun ShopListScreen(viewModel: MainComposeViewModel = hiltViewModel()) {
    val state = viewModel.state.value

    var componentHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Scaffold(

        floatingActionButton = {
            FloatingActionButton(modifier = Modifier
                .onGloballyPositioned {
                    componentHeight = with(density) {
                        it.size.height.toDp() + 32.dp
                    }
                },
                shape = CircleShape,
                onClick = {}
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumnSwappable(
            items = state.shopList,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(bottom = componentHeight),
            onSwap = viewModel::dragShopItem,
            onRemove = viewModel::deleteShopItem,
            onToggle = viewModel::changeEnableState
        )
    }
}




