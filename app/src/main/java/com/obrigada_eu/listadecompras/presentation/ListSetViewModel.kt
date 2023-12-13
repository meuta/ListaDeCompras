package com.obrigada_eu.listadecompras.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.obrigada_eu.listadecompras.domain.GetAllListsWithItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class ListSetViewModel @Inject constructor(
    private val getAllListsWithItemsUseCase: GetAllListsWithItemsUseCase,
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    val allListsWithItems = getAllListsWithItemsUseCase().asLiveData()

}