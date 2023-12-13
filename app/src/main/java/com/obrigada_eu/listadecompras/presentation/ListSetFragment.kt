package com.obrigada_eu.listadecompras.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = listSetViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupRecyclerView()
        setupClickListener()

        listSetViewModel.allListsWithItems.observe(viewLifecycleOwner) {
            listSetAdapter.submitList(it)
            Log.d("ListSetFragment", "allListsWithItems.observe =\n $it")
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