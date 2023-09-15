package com.example.listadecompras.presentation.shop_list_screen

import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.presentation.shop_list_screen.components.LazyColumnSwappable

@Composable
fun ShopListScreen(
    viewModel: MainComposeViewModel = hiltViewModel(),
    onItemClick: (ShopItem) -> Unit,
    onFabClick: () -> Unit
) {
    val state = viewModel.state.value

    var fabHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    val configuration = LocalConfiguration.current
    val orientation = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "Landscape"
    } else {
        "Portrait"
    }

    Scaffold(

        floatingActionButton = {
            FloatingActionButton(modifier = Modifier
                .onGloballyPositioned {
                    fabHeight = with(density) {
                        it.size.height.toDp()
                    }
                },
                shape = CircleShape,
                onClick = onFabClick
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        LazyColumnSwappable(
            items = state.shopList,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (orientation == "Portrait") fabHeight + 32.dp else 0.dp),
            onSwap = viewModel::dragShopItem,
            onRemove = viewModel::deleteShopItem,
            onToggle = viewModel::changeEnableState,
            onItemClick = onItemClick
        )
    }
}




