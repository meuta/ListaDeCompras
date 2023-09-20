package com.example.listadecompras.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.ShopListScreen.route
                    ) {
                        composable(route = Screen.ShopListScreen.route) {
                            ShopListScreen(navController = navController)
                        }
                        composable(
                            route = Screen.ShopItemScreen.route +
                                    "?itemId={itemId}&screenMode={screenMode}",
                            arguments = listOf(
                                navArgument(
                                    name = "itemId"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                                navArgument(
                                    name = "screenMode"
                                ) {
                                    type = NavType.StringType
                                    defaultValue = ""
                                },
                            )
                        )
                        {
                            val screenMode = it.arguments?.getString("screenMode") ?: ""
                            ShopItemScreen(navController = navController, screenMode = screenMode)
                        }
                        composable(
                            route = Screen.TwoPaneScreen.route +
                                    "?itemId={itemId}&screenMode={screenMode}",
                            arguments = listOf(
                                navArgument(
                                    name = "itemId"
                                ) {
                                    type = NavType.IntType
                                    defaultValue = -1
                                },
                                navArgument(
                                    name = "screenMode"
                                ) {
                                    type = NavType.StringType
                                    defaultValue = ""
                                },
                            )
                        )
                        {
                            val screenMode = it.arguments?.getString("screenMode") ?: ""
                            TwoPaneScreen(navController = navController, screenMode = screenMode)
                        }
                    }
                }
            }
        }
    }
}