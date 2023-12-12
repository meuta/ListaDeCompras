package com.obrigada_eu.listadecompras.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.obrigada_eu.listadecompras.databinding.FragmentListSetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListSetFragment : Fragment() {


    private val viewModel: ListSetViewModel by viewModels()

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

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

    }

    companion object {
        fun newInstance() = ListSetFragment()
    }

}