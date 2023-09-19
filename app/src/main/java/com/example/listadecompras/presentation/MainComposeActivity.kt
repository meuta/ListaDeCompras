package com.example.listadecompras.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.presentation.shop_item_screen.ShopItemScreen
import com.example.listadecompras.presentation.shop_list_screen.ShopListScreen
import com.example.listadecompras.ui.theme.ListaDeComprasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            ListaDeComprasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    val navController = rememberNavController()
//                    NavHost(
//                        navController = navController,
//                        startDestination = Screen.ShopListScreen.route
//                    ) {
//                        composable(route = Screen.ShopListScreen.route) {
                            ShopListScreen(
//                                navController = navController,
                                fragmentManager = supportFragmentManager
//                                onItemClick = this@MainComposeActivity::onClickShopItem,
                            )
//                        }
//                            composable(
//
//                            ) {
//
//                                ShopItemScreen(
//                                    navController = navController
//                                )
//                            }
//                    }
                }
            }
        }

    }



//    private fun isOnePaneMode(): Boolean {
//        return binding.shopItemContainer == null
//    }

//    private fun launchFragment(fragment: Fragment) {
//        with(supportFragmentManager) {
//            popBackStack()
//            beginTransaction()
//                .replace(R.id.shop_item_container, fragment)    //adding fragment to container
//                .addToBackStack(null)
//                .commit()
//        }
//    }


//    private fun onClickShopItem(shopItem: ShopItem) {
//        if (isOnePaneMode()) {
//            val intent = ShopItemComposeActivity.newIntentEditItem(this, shopItem.id)
//            startActivity(intent)
//        } else {
//            val fragment = ShopItemComposeFragment.newInstanceEditItem(shopItem.id)
//            launchFragment(fragment)
//        }
//    }

//    private fun onClickFab() {
//        if (isOnePaneMode()) {
//            val intent = ShopItemComposeActivity.newIntentAddItem(this)
//            startActivity(intent)
//        } else {
//            val fragment = ShopItemComposeFragment.newInstanceAddItem()
//            launchFragment(fragment)
//        }
//    }
}