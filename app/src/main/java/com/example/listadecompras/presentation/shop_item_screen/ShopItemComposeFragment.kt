package com.example.listadecompras.presentation.shop_item_screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.listadecompras.domain.ShopItem
import com.example.listadecompras.presentation.ShopItemComposeActivity
import com.example.listadecompras.ui.theme.ListaDeComprasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopItemComposeFragment : Fragment() {

    private val viewModel: ShopItemComposeViewModel by viewModels()

    private var screenMode = MODE_UNKNOWN
    private var itemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("ShopItemFragment", "onCreate")
        super.onCreate(savedInstanceState)
        parseParams()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ListaDeComprasTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
//                        ShopItemEditPane(
//                            itemName = viewModel.shopItemEditName,
//                            itemCount = viewModel.shopItemEditCount,
//                            showErrorName = viewModel.showErrorName,
//                            showErrorCount = viewModel.showErrorCount,
//                            onNameChange = { name -> viewModel.onNameChanged(name) },
//                            onCountChange = { count -> viewModel.onCountChanged(count) },
//                            onClick = { viewModel.onSaveClick() }
//                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchRightMode()

        observeViewModel()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Toast.makeText(this.context, "Success", Toast.LENGTH_SHORT).show()

    }


    private fun observeViewModel() {
        viewModel.closeScreen.observe(viewLifecycleOwner) {
            Log.d("closeScreenSubscribeTest", it.toString())
            activity?.supportFragmentManager?.popBackStack()
            activity?.let { activity -> if (activity is ShopItemComposeActivity) activity.onBackPressed() }
        }
    }

    private fun launchRightMode() {
        when (screenMode) {
            MODE_ADD -> launchAddMode()
            MODE_EDIT -> launchEditMode()
        }
    }


    private fun parseParams() {

        val args = requireArguments()
        if (!args.containsKey(SECOND_SCREEN_MODE)) {
            throw RuntimeException("Param second_screen_mode is absent")
        }

        val mode = args.getString(SECOND_SCREEN_MODE)
        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown second_screen_mode: $mode")
        }
        screenMode = mode

        if (screenMode == MODE_EDIT && itemId == ShopItem.UNDEFINED_ID) {
            if (!args.containsKey(SHOP_ITEM_ID)) {
                throw RuntimeException("Param shop_item_id is absent")
            }
            itemId = args.getInt(SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
            Log.d("ShopItemActivity", "id = $itemId")
        }
    }

    private fun launchAddMode() {
        viewModel.getZeroItem()
        viewModel.saveClick = { viewModel.addShopItem() }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(itemId)
        viewModel.saveClick = { viewModel.editShopItem() }
    }


    companion object {
        private const val SECOND_SCREEN_MODE = "second_screen_mode"
        private const val SHOP_ITEM_ID = "shop_item_id"
        private const val MODE_ADD = "mode_add"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_UNKNOWN = ""


        fun newInstanceAddItem(): ShopItemComposeFragment {
            return ShopItemComposeFragment().apply {
                arguments = Bundle().apply {
                    putString(SECOND_SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(itemId: Int): ShopItemComposeFragment {
            return ShopItemComposeFragment().apply {
                arguments = Bundle().apply {
                    putString(SECOND_SCREEN_MODE, MODE_EDIT)
                    putInt(SHOP_ITEM_ID, itemId)
                }
            }
        }
    }
}