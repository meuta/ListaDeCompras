package com.obrigada_eu.listadecompras.presentation.list_set

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
            listSetViewModel.updateUiState(false, false, null, null, null)
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

    private fun handleIntent(intent: Intent) {
//        Log.d(TAG, "handleIntent: intent = $intent")

        when (intent.action) {

            Intent.ACTION_SEND -> {

                setDefaultCardNewList()

                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                } else {
                    @Suppress("DEPRECATION") intent.getParcelableExtra(Intent.EXTRA_STREAM)
                }

                uri?.let {
//                    Log.d(TAG, "handleIntent: myFilePath = ${it.path}")
                    val fileName = listSetViewModel.getFileName(it)
                    listSetViewModel.addShopList(fileName, true, null, it)
                }
            }

            Intent.ACTION_VIEW -> {

                setDefaultCardNewList()

                when (intent.type) {

                    "text/plain" -> {

                        val data: Uri? = intent.data
//                        Log.d(TAG, "handleIntent: data = $data")
//                        Log.d(TAG, "handleIntent: myFilePath = ${data?.path}")
                        data?.let {
                            if (it.path?.take(5) == "/tree") {

                                val myFilePath = it.lastPathSegment
                                val parts = myFilePath?.split("/")
                                val myFileName = parts?.last()?.dropLast(4)
//                                Log.d(TAG, "handleIntent: myFilePath = $myFilePath")
//                                Log.d(TAG, "handleIntent: myFileName = $myFileName")
                                listSetViewModel.addShopList(myFileName, true, myFilePath)
                            } else {

                                val fileName = listSetViewModel.getFileName(it)
                                listSetViewModel.addShopList(fileName, true, null, it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setDefaultCardNewList() {
        listSetViewModel.resetUserCheckedDifferNames()
        listSetViewModel.resetErrorInputNameTitle()
        listSetViewModel.resetErrorInputNameContent()
        listSetViewModel.resetListNameFromContent()

        listSetViewModel.setCurrentListId(ShopList.UNDEFINED_ID)
    }


    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                listSetViewModel.fileWithoutErrors.collect {
                    Log.d(TAG, "observeViewModel: fileWithoutErrors = $it")
                    if (!it) {
                        Toast.makeText(
                            this@ListSetActivity,
                            "File reading error",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        delay(100)
                        listSetViewModel.resetFileWithoutErrors()
                        listSetViewModel.resetErrorInputNameTitle()
                        listSetViewModel.resetErrorInputNameContent()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                listSetViewModel.cardNewListVisibilityStateFlow.collect {isVisible ->
                    Log.d(TAG, "observeViewModel: cardNewListVisibilityStateFlow.collect = $isVisible")
                    with(binding){
                        if (!isVisible) {
                            toolbarListSetActivity.menu.setGroupVisible(R.id.list_set_menu_group, true)
                        } else {
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
                listSetViewModel.updateUiState(false, false, null, null, null)
                if (
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
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
                // perform the action as soon as authorisation is granted
                loadFilesList()

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

        setupFilesListView()
        binding.filesList.visibility = View.VISIBLE
    }

    private fun setupFilesListView() {
        listSetViewModel.loadFilesList()
        listSetViewModel.filesList.observe(this) {
            it?.let {
                val listAdapter =
                    ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, it)
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
            }
//            Log.d("ListSetActivity", "filesList.observe = $it")
        }
        filesListBackPressedCallback.isEnabled = true
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

        fun newIntent(context: Context): Intent {
            return Intent(context, ListSetActivity::class.java)
        }
    }
}