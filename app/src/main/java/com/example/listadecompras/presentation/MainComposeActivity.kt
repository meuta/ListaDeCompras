package com.example.listadecompras.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.example.listadecompras.R
import com.example.listadecompras.databinding.ActivityComposeMainBinding
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.presentation.shop_list_screen.ShopListScreen
import com.example.listadecompras.ui.theme.ListaDeComprasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainComposeActivity : AppCompatActivity(), ShopItemComposeFragment.OnEditingFinishedListener {

    private lateinit var binding: ActivityComposeMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityComposeMainBinding.inflate(layoutInflater)
        val view = binding.root
        binding.composeView?.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                ListaDeComprasTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ShopListScreen(
                            onItemClick = this@MainComposeActivity::onClickShopItem,
                            onFabClick = this@MainComposeActivity::onClickFab
                        )
                    }
                }
            }
        }

        setContentView(view)
    }


    override fun onEditingFinished() {
        supportFragmentManager.popBackStack()
    }

    private fun isOnePaneMode(): Boolean {
        return binding.shopItemContainer == null
    }

    private fun launchFragment(fragment: Fragment) {
        with(supportFragmentManager) {
            popBackStack()
            beginTransaction()
                .replace(R.id.shop_item_container, fragment)    //adding fragment to container
                .addToBackStack(null)
                .commit()
        }
    }


    private fun onClickShopItem(shopItem: ShopItem) {
        if (isOnePaneMode()) {
            val intent = ShopItemActivity.newIntentEditItem(this, shopItem.id)
            startActivity(intent)
        } else {
            val fragment = ShopItemComposeFragment.newInstanceEditItem(shopItem.id)
            launchFragment(fragment)
        }
    }

    private fun onClickFab() {
        if (isOnePaneMode()) {
            val intent = ShopItemActivity.newIntentAddItem(this)
            startActivity(intent)
        } else {
            val fragment = ShopItemComposeFragment.newInstanceAddItem()
            launchFragment(fragment)
        }
    }
}