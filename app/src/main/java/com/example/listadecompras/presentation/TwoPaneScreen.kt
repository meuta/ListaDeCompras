package com.example.listadecompras.presentation

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
import com.example.listadecompras.presentation.shop_item_screen.ShopItemEditPane
import com.example.listadecompras.presentation.shop_list_screen.components.LazyColumnSwappable

@Composable
fun TwoPaneScreen(
    navController: NavController,
    screenMode: String,
    viewModel: SingleViewModel = hiltViewModel()
) {

    val configuration = LocalConfiguration.current
    val orientation = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "Landscape"
    } else {
        "Portrait"
    }

    when (screenMode) {
        "modeEdit" -> viewModel.saveClick = { viewModel.editShopItem() }
        "modeAdd" -> viewModel.saveClick = { viewModel.addShopItem() }
    }
    val state = viewModel.state.value
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
                    navController.navigateUp()
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
                    navController.navigateUp()
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
            if (orientation == "Landscape") {

                ShopItemEditPane(
                    modifier = Modifier
                        .fillMaxSize()
                        .layoutId("itemPane"),
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

}


private fun constraints(orientation: String): ConstraintSet {
    return ConstraintSet {
        val shopList = createRefFor("shopList")
        val fabAdd = createRefFor("fabAdd")

        if (orientation == "Landscape") {
            val startGuideline = createGuidelineFromAbsoluteRight(0.5f)
            val itemPane = createRefFor("itemPane")

            constrain(shopList) {
                linkTo(parent.top, parent.bottom)
                linkTo(parent.start, startGuideline)
                width = Dimension.fillToConstraints
            }
            constrain(fabAdd) {
                bottom.linkTo(parent.bottom)
                end.linkTo(startGuideline)
            }
            constrain(itemPane) {
                linkTo(parent.top, parent.bottom)
                linkTo(startGuideline, parent.end)
                width = Dimension.fillToConstraints
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

