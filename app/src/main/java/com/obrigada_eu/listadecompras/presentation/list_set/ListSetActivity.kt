package com.obrigada_eu.listadecompras.presentation.list_set

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
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
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.databinding.ActivityListSetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListSetBinding

    private val listSetViewModel: ListSetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListSetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.list_set_container, ListSetFragment.newInstance())
                .commit()

            intent?.let {
                handleIntent(it)
            }
        }

        setupActionBar()
        setOnBackPressedCallback()
        observeViewModel()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleIntent(it)
        }
    }

    private fun handleIntent(intent: Intent) {
        Log.d(TAG, "handleIntent: intent = $intent")

        val data: Uri? = intent.data

        if (intent.action == Intent.ACTION_SEND) {

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                intent.getParcelableExtra(Intent.EXTRA_STREAM)
            }

            uri?.let {
                // Update UI to reflect image being shared
                Log.d(TAG, "handleIntent: uri = $it")
                val myFilePath = it.path
                Log.d(TAG, "handleIntent: myFilePath = $myFilePath")

                val fileName = getFileName(it)?.dropLast(4)
                listSetViewModel.addShopList(fileName, true, null, it)
            }

        } else {
            if (intent.action == Intent.ACTION_VIEW && intent.type == "text/plain") {
                val myFilePath = data?.lastPathSegment
                val parts = myFilePath?.split("/")
                val myFileName = parts?.last()?.dropLast(4)
                Log.d(TAG, "handleIntent: myFilePath = $myFilePath")
                Log.d(TAG, "handleIntent: myFileName = $myFileName")
                listSetViewModel.addShopList(myFileName, true, myFilePath)
            }
        }

    }


    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.let {
                it.use { cur ->
                    val nameIndex = cur.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cur.moveToFirst()
                    result = cur.getString(nameIndex)
                }
            }
        }
        if (result == null) {
            result = uri.path
            result?.let{
                val cut = it.lastIndexOf('/')
                if (cut != -1) {
                    result = it.substring(cut + 1)
                }
            }
        }
        return result
    }

    private fun observeViewModel() {
        listSetViewModel.fileWithoutErrors.observe(this) {
            if (!it) {
                Toast.makeText(this, "File reading error", Toast.LENGTH_LONG).show()
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

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q
                    && ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
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
                Toast.makeText(
                    this,
                    "Storage permissions granted,\nnow you can see the list of files",
                    Toast.LENGTH_LONG
                ).show()
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
                    Log.d("filesList.setOnItemClickListener", "element = $fileName")
                    fileName?.let { name ->
                        loadFromTxtFile(name)
                    }

                    parent.visibility = View.GONE
                    filesListBackPressedCallback.isEnabled = false
                }
            }
            Log.d("ListSetActivity", "filesList.observe = $it")
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