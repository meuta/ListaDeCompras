package com.obrigada_eu.listadecompras.presentation.shop_list

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ActivityShopListBinding
import com.obrigada_eu.listadecompras.presentation.SwipeSwapListFragment
import com.obrigada_eu.listadecompras.presentation.shop_item.ShopItemActivity
import com.obrigada_eu.listadecompras.presentation.shop_item.ShopItemFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ShopListActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener,
    SwipeSwapListFragment.OnFabClickListener, SwipeSwapListFragment.OnListItemClickListener {

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
                with(supportFragmentManager) {
                    val fragments = fragments
                    if (fragments.isNotEmpty() && fragments.last() is ShopItemFragment) {
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
            Log.d("ShopListActivity", "allListsWithItems.observe =\n $it")
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_txt -> {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    + ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        STORAGE_PERMISSION_CODE
                    )
                } else {
                    exportListToTxt()
                }
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                Toast.makeText(this, "Storage permissions granted,\nnow you can save the file", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Storage permissions denied,\nyou cannot save the file", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun exportListToTxt() {
        shopListViewModel.exportListToTxt()
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath + File.separator.toString() + this.resources.getString(R.string.app_name)
        Toast.makeText(this, "List has been saved to the directory:\n$dir", Toast.LENGTH_LONG).show()
    }

    private fun setupEditText() {
        with(binding) {
            etToolbarShopListActivity.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
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
                        inputMethodManager.hideSoftInputFromWindow(
                            etToolbarShopListActivity.windowToken,
                            0
                        )
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

    override fun onFabClick(listId: Int?) {
        listId?.let {
            if (isOnePaneMode()) {
                val intent = ShopItemActivity.newIntentAddItem(this@ShopListActivity, it)
                startActivity(intent)
            } else {
                val fragment = ShopItemFragment.newInstanceAddItem(it)
                launchFragment(fragment)
            }
        }
    }

    companion object {

        private const val STORAGE_PERMISSION_CODE = 101

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ShopListActivity::class.java)
            return intent
        }

    }
}