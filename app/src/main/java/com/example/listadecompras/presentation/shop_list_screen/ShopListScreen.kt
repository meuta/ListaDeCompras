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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.listadecompras.presentation.Screen
import com.example.listadecompras.presentation.SingleViewModel
import com.example.listadecompras.presentation.shop_list_screen.components.LazyColumnSwappable

@Composable
fun ShopListScreen(
    navController: NavController,
    viewModel: SingleViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    val configuration = LocalConfiguration.current
    val orientation = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "Landscape"
    } else {
        "Portrait"
    }

    Scaffold() { padding ->
        ConstraintLayout(
            constraints(orientation),
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumnSwappable(
                items = state.shopList,
                modifier = Modifier
                    .fillMaxSize()
                    .layoutId("shopList"),
                onSwap = viewModel::dragShopItem,
                onRemove = viewModel::deleteShopItem,
                onToggle = viewModel::changeEnableState,
                onItemClick = {
                    if (orientation == "Portrait") {
                        navController.navigate(
                            Screen.ShopItemScreen.route +
                                    "?itemId=${it.id}&screenMode=modeEdit"
                        )
                    } else {
                        navController.navigate(
                            Screen.TwoPaneScreen.route +
                                    "?itemId=${it.id}&screenMode=modeEdit"
                        )
                    }
                }
            )
            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .layoutId("fabAdd"),
                shape = CircleShape,
                onClick = {
                    if (orientation == "Portrait") {
                        navController.navigate(
                            Screen.ShopItemScreen.route +
                                    "?screenMode=modeAdd"
                        )
                    } else {
                        navController.navigate(
                            Screen.TwoPaneScreen.route +
                                    "?screenMode=modeAdd"
                        )
                    }
                }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
        }
    }
}

private fun constraints(orientation: String): ConstraintSet {
    return ConstraintSet {
        val shopList = createRefFor("shopList")
        val fabAdd = createRefFor("fabAdd")

        if (orientation == "Landscape") {

            val startGuideline = createGuidelineFromAbsoluteRight(0.5f)

            constrain(shopList) {
                linkTo(parent.top, parent.bottom)
                linkTo(parent.start, startGuideline)
                width = Dimension.fillToConstraints
            }
            constrain(fabAdd) {
                bottom.linkTo(parent.bottom)
                end.linkTo(startGuideline)
            }
        } else {
            constrain(shopList) {
                linkTo(parent.top, fabAdd.top)
                linkTo(parent.start, parent.end)
                height = Dimension.fillToConstraints
            }
            constrain(fabAdd) {
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
        }
    }
}



