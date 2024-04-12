package com.obrigada_eu.listadecompras.presentation.shop_list

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ActivityShopListBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.SwipeSwapListFragment
import com.obrigada_eu.listadecompras.presentation.shop_item.ShopItemActivity
import com.obrigada_eu.listadecompras.presentation.shop_item.ShopItemFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShopListActivity : AppCompatActivity(), ShopItemFragment.OnEditingFinishedListener,
    SwipeSwapListFragment.OnFabClickListener, SwipeSwapListFragment.OnListItemClickListener {

    private val shopListViewModel: ShopListViewModel by viewModels()

    private lateinit var binding: ActivityShopListBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

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
        setOnBackPressedCallback()
    }

    private fun observeViewModel() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                shopListViewModel.shopListIdFlow.collect{
//            Log.d("ShopListActivity", "shopListIdLD.collect = $it")
                    if (it == ShopList.UNDEFINED_ID){
                        this@ShopListActivity.finish()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shopListViewModel.shopListNameFlow.collect {
//            Log.d(TAG, "shopListName.collect = $it")
                    if (it != binding.tvToolbarShopListActivity.text) {
                        binding.tvToolbarShopListActivity.text = it
                    }
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                shopListViewModel.errorInputName.collect {
//                    Log.d(TAG, "errorInputName.collect = $it")
                    with(binding) {
                        if (it) {
                            tvErrorToolbarShopListActivity.text =
                                this@ShopListActivity.getString(R.string.error_input_list_name)
                            tvErrorToolbarShopListActivity.visibility = View.VISIBLE
                        } else {
                            tvErrorToolbarShopListActivity.text = ""
                            tvErrorToolbarShopListActivity.visibility = View.GONE
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                shopListViewModel.renameListAppearance.collect {
//                    Log.d(TAG, "renameListAppearance.collect = $it")
                    with(binding) {
                        if (!it) {
                            buttonSaveListName.visibility = View.INVISIBLE

                            toolbarShopListActivity.navigationIcon = ContextCompat.getDrawable(
                                this@ShopListActivity, R.drawable.ic_arrow_back_normal_56
                            )
                            toolbarShopListActivity.menu.setGroupVisible(R.id.shop_list_menu_group, true)

                            etToolbarShopListActivity.visibility = View.INVISIBLE
                            etToolbarShopListActivity.clearFocus()
                            etToolbarShopListActivity.setText("")
                            val inputMethodManager =
                                this@ShopListActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(etToolbarShopListActivity.windowToken, 0)
                            lifecycleScope.launch{
                                delay(25)
                                tvToolbarShopListActivity.visibility = View.VISIBLE
                                renameListAppearanceCallback.isEnabled = false
                            }
                            coverView.visibility = View.GONE

                        } else {
                            coverView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED){
                shopListViewModel.intent.collect {
//            Log.d(TAG, "observeViewModel: intent.collect = $it")
                    it?.let {
                        startActivity(it)
                        shopListViewModel.resetIntent()
                        shopListViewModel.setRenameListAppearance(false)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                shopListViewModel.fileSaved.collect {
//                    Log.d(TAG, "observeViewModel: fileWithoutErrors = $it")
                    it?.let {

                        Toast.makeText(
                            this@ShopListActivity,
                            if (it.isNotEmpty()) "File saved to:\n$it" else "File saving error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        shopListViewModel.resetFileSaved()
                    }
                }
            }
        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarShopListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_normal_56)
        }
        binding.toolbarShopListActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        setupEditText()
        setupCoverView()
    }

    private fun setupCoverView(){

        with(binding){
            coverView.setOnTouchListener { view, event ->
                view.performClick()
                if (event.action == MotionEvent.ACTION_DOWN ) {
                    lifecycleScope.launch {
                        if (shopListViewModel.renameListAppearance.first()) {
                            buttonSaveListName.setBackgroundResource(R.color.whitish)
                            toolbarShopListActivity.navigationIcon = ContextCompat.getDrawable(
                                this@ShopListActivity, R.drawable.ic_arrow_back_alert_56
                            )

                            delay(300)

                            buttonSaveListName.setBackgroundResource(R.color.item_background_color)
                            toolbarShopListActivity.navigationIcon = ContextCompat.getDrawable(
                                this@ShopListActivity, R.drawable.ic_arrow_back_purple_56
                            )
                        }
                    }
                }
                true
            }
        }
    }

    private val renameListAppearanceCallback = object : OnBackPressedCallback(
        true // default to enabled
    ) {
        override fun handleOnBackPressed() {
            lifecycleScope.launch {
                if (shopListViewModel.renameListAppearance.first()) {
                    shopListViewModel.setRenameListAppearance(false)
                }
            }
        }
    }

    private fun setOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this, renameListAppearanceCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_txt -> {

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
                    && ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    + ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
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


            R.id.action_share_file -> {
                shareTxtList()
            }


            R.id.action_rename_list -> {
                shopListViewModel.setRenameListAppearance(true)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareTxtList(){
        shopListViewModel.shareTxtList()
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
                // perform the action as soon as authorisation is granted
                exportListToTxt()

            } else {
                Toast.makeText(
                    this,
                    "Storage permissions denied,\nyou cannot save the file",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun exportListToTxt() {
        shopListViewModel.exportListToTxt()
//        val dir =
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//                .absolutePath + File.separator.toString() + this.resources.getString(
//                R.string.app_name
//            )
//        Toast.makeText(this, "List has been saved to the directory:\n$dir", Toast.LENGTH_LONG)
//            .show()
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        lifecycleScope.launch {
            if (shopListViewModel.renameListAppearance.first() && hasFocus) {
                with(binding) {
                    tvToolbarShopListActivity.visibility = View.INVISIBLE
                    etToolbarShopListActivity.setText(tvToolbarShopListActivity.text)
                    etToolbarShopListActivity.setSelection(etToolbarShopListActivity.text.length)
                    etToolbarShopListActivity.visibility = View.VISIBLE

                    etToolbarShopListActivity.requestFocus()
                    val inputMethodManager =
                        this@ShopListActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(etToolbarShopListActivity, 0)
                    buttonSaveListName.visibility = View.VISIBLE

                    toolbarShopListActivity.navigationIcon = ContextCompat.getDrawable(
                        this@ShopListActivity, R.drawable.ic_arrow_back_purple_56
                    )

                    toolbarShopListActivity.menu.setGroupVisible(R.id.shop_list_menu_group, false)

                    renameListAppearanceCallback.isEnabled = true
                }
            }
        }
    }

    private fun setupEditText() {
        with(binding) {

            buttonSaveListName.setOnClickListener {
                shopListViewModel.updateShopListName(etToolbarShopListActivity.trimmedText())
                lifecycleScope.launch {
                    if (!shopListViewModel.errorInputName.first()) {
                        shopListViewModel.setRenameListAppearance(false)
                    }
                }
            }
        }
        addTextChangedListeners()
    }

    private fun EditText.trimmedText() = this.text.toString().let { content ->
        content.trim().let { trimmedContent ->
            if (trimmedContent != content) {
                this.setText(trimmedContent)
                this.setSelection(trimmedContent.length)
                trimmedContent
            } else {
                content
            }
        }
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
            val intent = ShopItemActivity.newIntentEditItem(
                this,
                itemId,
                binding.tvToolbarShopListActivity.text.toString()
            )
            startActivity(intent)
        } else {
            val fragment = ShopItemFragment.newInstanceEditItem(itemId)
            launchFragment(fragment)
        }
    }

    override fun onFabClick() {

        lifecycleScope.launch {
            shopListViewModel.shopListIdFlow.first().let {
                if (isOnePaneMode()) {
                    val intent = ShopItemActivity.newIntentAddItem(
                        this@ShopListActivity,
                        it,
                        binding.tvToolbarShopListActivity.text.toString()
                    )
                    startActivity(intent)
                } else {
                    val fragment = ShopItemFragment.newInstanceAddItem(it)
                    launchFragment(fragment)
                }
            }
        }
    }

    companion object {

        private const val STORAGE_PERMISSION_CODE = 101
        private const val TAG = "ShopListActivity"

        fun newIntent(context: Context): Intent {
            return Intent(context, ShopListActivity::class.java)
        }

    }
}