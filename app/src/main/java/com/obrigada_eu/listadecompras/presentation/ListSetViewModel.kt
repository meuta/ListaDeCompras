package com.obrigada_eu.listadecompras.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.domain.AddShopListUseCase
import com.obrigada_eu.listadecompras.domain.DeleteShopListUseCase
import com.obrigada_eu.listadecompras.domain.GetAllListsWithoutItemsUseCase
import com.obrigada_eu.listadecompras.domain.GetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.SetCurrentListIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListSetViewModel @Inject constructor(
    getAllListsWithoutItemsUseCase: GetAllListsWithoutItemsUseCase,
    private val addShopListUseCase: AddShopListUseCase,
    private val deleteShopListUseCase: DeleteShopListUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase,
    private val getCurrentListIdUseCase: GetCurrentListIdUseCase
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var _errorInputName = MutableLiveData<Boolean>()
    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    private val _shopListIdLD = MutableLiveData<Int>()
    val shopListIdLD: LiveData<Int>
        get() = _shopListIdLD

    val allListsWithoutItems = getAllListsWithoutItemsUseCase().asLiveData(scope.coroutineContext)

    init {
        viewModelScope.launch {
            getCurrentListIdUseCase().collect{
                Log.d("ListSetViewModel","init id = $it")
                _shopListIdLD.value = it
            }
        }
    }

    fun openShopList(listId: Int){
        scope.launch {
            setCurrentListIdUseCase(listId)
        }
    }


    fun addShopList(inputName: String?) {
        val name = parseName(inputName)
        val fieldsValid = validateInput(name)
        if (fieldsValid) {

            viewModelScope.launch {
                addShopListUseCase(name)
            }
        }
    }


    private fun validateInput(name: String): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }

        val names = allListsWithoutItems.value?.map { it.name }
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