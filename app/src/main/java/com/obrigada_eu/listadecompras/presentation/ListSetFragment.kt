package com.obrigada_eu.listadecompras.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.obrigada_eu.listadecompras.databinding.FragmentListSetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSetFragment : Fragment() {


    private val listSetViewModel: ListSetViewModel by viewModels()

    private lateinit var listSetAdapter: ListSetAdapter
    private var _binding: FragmentListSetBinding? = null
    private val binding: FragmentListSetBinding
        get() = _binding ?: throw RuntimeException("FragmentListSetBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setOnBackPressedCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                with(binding) {
                    if (cardNewList.visibility == View.VISIBLE) {
//                    val inputMethodManager =
//                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    inputMethodManager.hideSoftInputFromWindow(buttonCreateList.windowToken, 0)
                        etListName.setText("")
                        cardNewList.visibility = View.GONE
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner
            callback
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = listSetViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()
        setupClickListener()
        addTextChangedListeners()
        setupButtons()
        listSetViewModel.allListsWithItems.observe(viewLifecycleOwner) {
            listSetAdapter.submitList(it)
            Log.d("ListSetFragment", "allListsWithItems.observe =\n $it")
        }
    }

    private fun setupButtons() {
        with(binding) {
            buttonAddShopList.setOnClickListener {
                if (cardNewList.visibility == View.GONE) {
                    cardNewList.visibility = View.VISIBLE
                    etListName.setText("New List")
                    etListName.requestFocus()
                    etListName.setSelection(etListName.text.length)

                    val inputMethodManager =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(etListName, 0)
                }
            }
            buttonCreateList.setOnClickListener { view ->
                if (etListName.text.isNotEmpty()) {
                    listSetViewModel.addShopList(etListName.text?.toString())
                    val isError = listSetViewModel.errorInputName.value ?: true
                    if (!isError) {
                        etListName.setText("")
                        cardNewList.visibility = View.GONE
                        val inputMethodManager =
                            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }
        }
    }

    private fun addTextChangedListeners() {
        val inputErrorListener = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(binding) {
                    if (etListName.text.hashCode() == s.hashCode()) {
                        listSetViewModel.resetErrorInputName()
                    }
                }
            }


            override fun afterTextChanged(s: Editable?) {
            }
        }
        with(binding) {
            etListName.addTextChangedListener(inputErrorListener)
        }
    }

    private fun setupRecyclerView() {
        listSetAdapter = ListSetAdapter()

        with(binding.rvListSet) {
            adapter = listSetAdapter

            layoutManager = LinearLayoutManager(
                this@ListSetFragment.context,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }


    private fun setupClickListener() {
        listSetAdapter.onListItemClickListener = {
            this.requireActivity().finish()
            val intent = MainActivity.newIntent(this.requireContext(), it.id)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance() = ListSetFragment()
    }

}