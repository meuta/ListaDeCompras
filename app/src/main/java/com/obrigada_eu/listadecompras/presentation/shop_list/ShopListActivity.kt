package com.obrigada_eu.listadecompras.presentation.shop_list

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ActivityShopListBinding
import com.obrigada_eu.listadecompras.presentation.shop_item.ShopItemActivity
import com.obrigada_eu.listadecompras.presentation.shop_item.ShopItemFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShopListActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener,
    ShopListFragment.OnFabClickListener, ShopListFragment.OnListItemClickListener {

    private val shopListViewModel: ShopListViewModel by viewModels()

    private lateinit var binding: ActivityShopListBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.shop_list_container, ShopListFragment.newInstance())
                .commit()
        } else {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                with(supportFragmentManager){
                    val fragments = fragments
                    if (fragments.isNotEmpty() && fragments.last() is ShopItemFragment){
                        popBackStack()
                    }
                }
            }
        }
    }

    private fun observeViewModel() {

        shopListViewModel.getShopListName()
        shopListViewModel.shopListName.observe(this) {
            Log.d("ShopListActivity", "shopListNameobserve = $it")
            setupActionBar(it)
        }

        shopListViewModel.allListsWithoutItems.observe(this) {
            Log.d("ShopListFragment", "allListsWithItems.observe =\n $it")
        }
    }


    private fun setupActionBar(name: String) {
        setSupportActionBar(binding.toolbarShopListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
            binding.etToolbarShopListActivity.setText(name)
        }
        binding.toolbarShopListActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        setupEditText()
    }

    private fun setupEditText(){
        with(binding){
            etToolbarShopListActivity.setOnFocusChangeListener {view, hasFocus ->
                if(hasFocus) {
                    buttonSaveListName.visibility = View.VISIBLE
                } else {
                    buttonSaveListName.visibility = View.INVISIBLE
                }
            }

            buttonSaveListName.setOnClickListener {
                if (etToolbarShopListActivity.text?.isNotEmpty() == true) {
                    shopListViewModel.updateShopListName(etToolbarShopListActivity.text.toString())
                    val isError = shopListViewModel.errorInputName.value ?: true
                    if (!isError) {
                        it.visibility = View.INVISIBLE
                        etToolbarShopListActivity.clearFocus()
                        val inputMethodManager =
                            this@ShopListActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(etToolbarShopListActivity.windowToken, 0)
                    }
                }
            }
        }
        addTextChangedListeners()
    }

    private fun addTextChangedListeners() {
        val inputErrorListener = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(binding) {
                    if (etToolbarShopListActivity.text.hashCode() == s.hashCode()) {
                        shopListViewModel.resetErrorInputName()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        with(binding) {
            etToolbarShopListActivity.addTextChangedListener(inputErrorListener)
        }
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
                .replace(R.id.shop_item_container, fragment)
                .addToBackStack(null).commit()
        }
    }


    override fun onListItemClick(itemId: Int) {
        if (isOnePaneMode()) {
            val intent = ShopItemActivity.newIntentEditItem(this, itemId)
            startActivity(intent)
        } else {
            val fragment = ShopItemFragment.newInstanceEditItem(itemId)
            launchFragment(fragment)
        }
    }

    override fun onFabClick(listId: Int) {
        if (isOnePaneMode()) {
            val intent = ShopItemActivity.newIntentAddItem(this@ShopListActivity, listId)
            startActivity(intent)
        } else {
            val fragment = ShopItemFragment.newInstanceAddItem(listId)
            launchFragment(fragment)
        }
    }

    companion object {

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ShopListActivity::class.java)
            return intent
        }

    }
}