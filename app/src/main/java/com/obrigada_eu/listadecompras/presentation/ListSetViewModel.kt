package com.obrigada_eu.listadecompras.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.domain.AddShopListUseCase
import com.obrigada_eu.listadecompras.domain.DeleteShopListUseCase
import com.obrigada_eu.listadecompras.domain.GetAllListsWithItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListSetViewModel @Inject constructor(
    getAllListsWithItemsUseCase: GetAllListsWithItemsUseCase,
    private val addShopListUseCase: AddShopListUseCase,
    private val deleteShopListUseCase: DeleteShopListUseCase
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName
//
//
//    private val _closeScreen = MutableLiveData<Unit>()
//    val closeScreen: LiveData<Unit>
//        get() = _closeScreen

    val allListsWithItems = getAllListsWithItemsUseCase().asLiveData()

    fun addShopList(inputName: String?) {
        val name = parseName(inputName)
        val fieldsValid = validateInput(name)
        if (fieldsValid) {

            viewModelScope.launch {
                addShopListUseCase(name)
            }
        }
    }

//    private fun finishView() {
//            _closeScreen.value = Unit
//    }

    private fun validateInput(name: String): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }

        val names = allListsWithItems.value?.map { it.name }
        Log.d("validateInput", "names = $names")
        names?.let {
            if (names.contains(name)) {
                _errorInputName.value = true
                Log.d("validateInput", "_errorInputName.value = ${_errorInputName.value}")
                result = false
            }
        }

        return result
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    fun resetErrorInputName() {
        _errorInputName.value = false
    }

    fun deleteShopList(id: Int) {
        scope.launch {
            deleteShopListUseCase(id)
        }
    }
}