package com.example.listadecompras.presentation.shop_list_screen

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.listadecompras.R
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.presentation.Screen
import com.example.listadecompras.presentation.ShopItemComposeActivity
import com.example.listadecompras.presentation.ShopItemComposeFragment
import com.example.listadecompras.presentation.shop_list_screen.components.LazyColumnSwappable

@Composable
fun ShopListScreen(
    navController: NavController,
    viewModel: MainComposeViewModel = hiltViewModel(),
//    onItemClick: (ShopItem) -> Unit,
//    onFabClick: () -> Unit
) {
    val state = viewModel.state.value

    val configuration = LocalConfiguration.current
    val orientation = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "Landscape"
    } else {
        "Portrait"
    }
    val context = LocalContext.current

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
//                onItemClick = onItemClick
            )
            FloatingActionButton(
                modifier = Modifier
                    .padding(16.dp)
                    .layoutId("fabAdd"),
                shape = CircleShape,
//                onClick = onFabClick
//                onClick = { navController.navigate(Screen.ShopItemScreen.route) }
                onClick = {
                    if (orientation == "Portrait"){
                        val intent = ShopItemComposeActivity.newIntentAddItem(context)
                        context.startActivity(intent)
                    } else {
//                        val fragment = ShopItemComposeFragment.newInstanceAddItem()
//                        launchFragment(fragment)
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



