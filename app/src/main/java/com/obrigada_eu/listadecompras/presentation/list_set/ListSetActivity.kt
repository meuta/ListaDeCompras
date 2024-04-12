package com.obrigada_eu.listadecompras.presentation.list_set

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ActivityListSetBinding
import com.obrigada_eu.listadecompras.domain.shop_list.ShopList
import com.obrigada_eu.listadecompras.presentation.shop_list.ShopListActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListSetBinding

    private val listSetViewModel: ListSetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
//        Log.d(TAG, "onCreate: intent = $intent")
        super.onCreate(savedInstanceState)
        binding = ActivityListSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            replaceListSetFragment()

            intent?.let {
                handleIntent(it)
            }
        }

        setupActionBar()
        setOnBackPressedCallback()
        observeViewModel()
    }


    override fun onNewIntent(intent: Intent?) {
//        Log.d(TAG, "onNewIntent: intent = $intent")
        super.onNewIntent(intent)
        intent?.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent?.let {
            listSetViewModel.updateUiState(
                cardNewListVisibility = false,
                showCreateListForFile = false,
                oldFileName = null,
                uri = null
            )
            with(binding.filesList) {
                if (visibility == View.VISIBLE) {
                    visibility = View.GONE
                }
            }
            handleIntent(it)
        }
    }

    private fun replaceListSetFragment() = supportFragmentManager.beginTransaction()
        .replace(R.id.list_set_container, ListSetFragment.newInstance())
        .commit()


    private fun startShopListActivity() {
        val intent = ShopListActivity.newIntent(this)
        startActivity(intent)
    }


    private fun handleIntent(intent: Intent) {
//        Log.d(TAG, "handleIntent: intent = $intent")

        when (intent.action) {

            Intent.ACTION_MAIN -> {
                lifecycleScope.launch {
                    if (listSetViewModel.shopListId.first() != ShopList.UNDEFINED_ID) {
                        startShopListActivity()
                    }
                }
            }

            Intent.ACTION_SEND -> {

                resetCardNewList()
                listSetViewModel.setCurrentListId(ShopList.UNDEFINED_ID)

                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION") intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }

                uri?.let {
//                    Log.d(TAG, "handleIntent: myFilePath = ${it.path}")
                    val fileName = listSetViewModel.getFileName(it)
                    listSetViewModel.addShopList(fileName, true, it)
                }
            }

            Intent.ACTION_VIEW -> {
//                Log.d(TAG, "handleIntent: intent.type = ${intent.type}")
                resetCardNewList()
                listSetViewModel.setCurrentListId(ShopList.UNDEFINED_ID)

                when (intent.type) {

                    "text/plain" -> {

                        val dataUri: Uri? = intent.data
//                        Log.d(TAG, "handleIntent: data = $dataUri")
//                        Log.d(TAG, "handleIntent: myFilePath = ${dataUri?.path}")
                        dataUri?.let {uri ->

                            val fileName = listSetViewModel.getFileName(uri)
                            listSetViewModel.addShopList(fileName, true, uri)
                        }
                    }
                }
            }
        }
    }

    private fun resetCardNewList() {
        with(listSetViewModel) {
            resetUserCheckedAlterName()
            resetErrorInputNameTitle()
            resetErrorInputNameContent()
            resetListNameFromContent()
        }
    }


    private fun observeViewModel() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                listSetViewModel.shopListId.collect { listId ->

//                    Log.d(TAG, "shopListIdLD.collect = $listId")
                    if (listId != ShopList.UNDEFINED_ID) {
                        startShopListActivity()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                listSetViewModel.fileReadingError.collect {
//                    Log.d(TAG, "observeViewModel: fileWithoutErrors = $it")
                    if (it) {
                        Toast.makeText(
                            this@ListSetActivity,
                            "File reading error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        listSetViewModel.resetFileWithoutErrors()

                        listSetViewModel.updateUiState(
                            cardNewListVisibility = false,
                            showCreateListForFile = false,
                            oldFileName = null,
                            uri = null
                        )
                        resetCardNewList()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                listSetViewModel.listSaved.collect {
//                    Log.d(TAG, "observeViewModel: fileWithoutErrors = $it")
                    it?.let {
                        Toast.makeText(
                            this@ListSetActivity,
                            if (it) "New list loaded" else "List loading error",
                            Toast.LENGTH_LONG
                        ).show()
                        listSetViewModel.resetListSaved()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                listSetViewModel.cardNewListVisibilityStateFlow.collect {isVisible ->
//                    Log.d(TAG, "observeViewModel: cardNewListVisibilityStateFlow.collect = $isVisible")
                    with(binding){
                        if (!isVisible) {
//                            Log.d(TAG, "observeViewModel: not visible")
                            toolbarListSetActivity.menu.setGroupVisible(R.id.list_set_menu_group, true)
                        } else {
//                            Log.d(TAG, "observeViewModel: visible")
                            delay(50)   // for change orientation
                            toolbarListSetActivity.menu.setGroupVisible(R.id.list_set_menu_group, false)
                        }
                    }
                }
            }
        }
    }

    private fun setupActionBar() {
        with(binding) {
            setSupportActionBar(toolbarListSetActivity)
            toolbarListSetActivity.setOnClickListener {
                if (filesList.visibility == View.VISIBLE) {
                    filesList.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_set_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_load_txt -> {
                listSetViewModel.updateUiState(
                    cardNewListVisibility = false,
                    showCreateListForFile = false,
                    oldFileName = null,
                    uri = null
                )
                if (
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
                    && ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                    listSetViewModel.setRequestForFileLoading(true)
                } else {
                    loadFilesList()
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
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                lifecycleScope.launch{
                    listSetViewModel.requestForFileLoading.first().let{
                        if (it){
                            // perform the action as soon as permission is granted
                            listSetViewModel.setRequestForFileLoading(false)
                            loadFilesList()
                        }
                    }
                }

            } else {
                Toast.makeText(
                    this,
                    "Storage permissions denied,\nyou cannot see the list of files",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loadFilesList() {
        listSetViewModel.loadFilesList().let {
            if (!it.isNullOrEmpty()) {
                val listAdapter = ArrayAdapter(this,
                    android.R.layout.simple_list_item_1,
                    it
                )
                binding.filesList.adapter = listAdapter
                binding.filesList.setOnItemClickListener { parent, _, position, _ ->
                    val fileName = listAdapter.getItem(position)?.dropLast(4)
//                    Log.d("filesList.setOnItemClickListener", "element = $fileName")
                    fileName?.let { name ->
                        loadFromTxtFile(name)
                    }
                    parent.visibility = View.GONE
                    filesListBackPressedCallback.isEnabled = false
                }
                filesListBackPressedCallback.isEnabled = true

                binding.filesList.visibility = View.VISIBLE

            } else {
                Toast.makeText(this, "No files in the directory\nDocuments/Lista de Compras", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun loadFromTxtFile(fileName: String) {
        listSetViewModel.addShopList(fileName, true)
    }

    private val filesListBackPressedCallback = object : OnBackPressedCallback(
        false // default to enabled
    ) {
        override fun handleOnBackPressed() {
            with(binding) {
                if (filesList.visibility == View.VISIBLE) {
                    filesList.visibility = View.GONE
                    isEnabled = false
                }
            }
        }
    }

    private fun setOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(
            this,
            filesListBackPressedCallback
        )
    }

    companion object {

        private const val STORAGE_PERMISSION_CODE = 101
        private const val TAG = "ListSetActivity"

    }
}