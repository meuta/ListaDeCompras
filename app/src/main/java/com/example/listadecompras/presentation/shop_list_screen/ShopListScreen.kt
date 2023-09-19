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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.presentation.Screen
import com.example.listadecompras.presentation.ShopItemComposeActivity
import com.example.listadecompras.presentation.ShopItemComposeFragment
import com.example.listadecompras.presentation.shop_item_screen.components.FragmentContainer
import com.example.listadecompras.presentation.shop_list_screen.components.LazyColumnSwappable

@Composable
fun ShopListScreen(
//    navController: NavController,
    fragmentManager: FragmentManager,
    viewModel: MainComposeViewModel = hiltViewModel(),
//    onItemClick: (ShopItem) -> Unit,
) {
    val state = viewModel.state.value

    val configuration = LocalConfiguration.current
    val orientation = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "Landscape"
    } else {
        "Portrait"
    }
    val context = LocalContext.current

    var fragment: ShopItemComposeFragment? by remember { mutableStateOf(null) }

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
                onClick = {
                    if (orientation == "Portrait") {
                        val intent = ShopItemComposeActivity.newIntentAddItem(context)
                        context.startActivity(intent)
                    } else {
                        fragment = ShopItemComposeFragment.newInstanceAddItem()

                    }
                }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
            fragment?.let {fragment ->
                if (orientation == "Landscape") {
                    FragmentContainer(
                        modifier = Modifier
                            .fillMaxSize()
                            .layoutId("fragmentContainer"),
                        fragmentManager = fragmentManager,
                        commit = {
                            replace(it, fragment)
                            addToBackStack(null)
                            commit()
                        }
                    )
                }
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
            val fragmentContainer = createRefFor("fragmentContainer")

            constrain(shopList) {
                linkTo(parent.top, parent.bottom)
                linkTo(parent.start, startGuideline)
                width = Dimension.fillToConstraints
            }
            constrain(fabAdd) {
                bottom.linkTo(parent.bottom)
                end.linkTo(startGuideline)
            }
            constrain(fragmentContainer) {
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



