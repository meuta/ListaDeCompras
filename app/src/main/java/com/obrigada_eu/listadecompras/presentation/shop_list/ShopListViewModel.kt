package com.obrigada_eu.listadecompras.presentation.shop_list

import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.domain.shop_item.DeleteShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.DragShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.EditShopItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.GetShopListUseCase
import com.obrigada_eu.listadecompras.domain.shop_item.ShopItem
import com.obrigada_eu.listadecompras.domain.shop_item.UndoDeleteItemUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.ExportListToTxtUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetAllListsWithoutItemsFlowUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetShopListNameUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.SetCurrentListIdUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.ShareTxtListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.UpdateShopListNameUseCase
import com.obrigada_eu.listadecompras.presentation.SwipeSwapViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel @Inject constructor(
    getAllListsWithoutItemsUseCase: GetAllListsWithoutItemsFlowUseCase,
    getShopListUseCase: GetShopListUseCase,
    private val deleteShopItemUseCase: DeleteShopItemUseCase,
    private val editShopItemUseCase: EditShopItemUseCase,
    private val dragShopItemUseCase: DragShopItemUseCase,
    getShopListNameUseCase: GetShopListNameUseCase,
    private val updateShopListNameUseCase: UpdateShopListNameUseCase,
    private val setCurrentListIdUseCase: SetCurrentListIdUseCase,
    getCurrentListIdUseCase: GetCurrentListIdUseCase,
    private val exportListToTxtUseCase: ExportListToTxtUseCase,
    private val shareTxtListUseCase: ShareTxtListUseCase,
    private val undoDeleteItemUseCase: UndoDeleteItemUseCase,
) : SwipeSwapViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    val shopListIdFlow: StateFlow<Int> = getCurrentListIdUseCase()

    val shopListNameFlow = getShopListNameUseCase(shopListIdFlow.value)

    val shopListFlow = getShopListUseCase(shopListIdFlow.value)

    private val _errorInputName = MutableStateFlow<Boolean>(false)
    val errorInputName: StateFlow<Boolean> = _errorInputName

    private val _renameListAppearance = MutableStateFlow<Boolean>(false)
    val renameListAppearance: StateFlow<Boolean> = _renameListAppearance

    private val allListsWithoutItemsStateFlow = getAllListsWithoutItemsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(1000),
        initialValue = emptyList()
    )

    private val _intent = MutableStateFlow<Intent?>(null)
    val intent: StateFlow<Intent?> = _intent


    private val _fileSaved: MutableStateFlow<String?>  = MutableStateFlow(null)
    val fileSaved: StateFlow<String?> = _fileSaved


    init {
        scope.launch{
            allListsWithoutItemsStateFlow.collect() {
//                Log.d(TAG, "allListsWithItems.collect =\n $it")
            }
        }
    }


    fun updateShopListIdState(listId: Int) {
        scope.launch {
            setCurrentListIdUseCase(listId)
        }
    }


    fun deleteShopItem(shopItem: ShopItem) {
        viewModelScope.launch {
            deleteShopItemUseCase(shopItem)
        }
    }

    fun changeEnableState(shopItem: ShopItem) {
        viewModelScope.launch {
            val newItem = shopItem.copy(enabled = !shopItem.enabled)
            editShopItemUseCase(newItem)
        }
    }

    fun dragShopItem(from: Int, to: Int) {
        viewModelScope.launch {
            dragShopItemUseCase(from, to)
        }
    }

    fun updateShopListName(inputName: String){
        val name = parseName(inputName)
        val fieldsValid = validateInput(name)
        if (fieldsValid) {
            scope.launch {
                updateShopListNameUseCase(shopListIdFlow.value, name)
            }
        }
    }


    private fun validateInput(name: String): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            return false
        }

        val names = allListsWithoutItemsStateFlow.value.map { it.name }
//        Log.d("validateInput", "namesSet = $names")

        if (names.contains(name)) {
            val id = allListsWithoutItemsStateFlow.value.find { it.name == name }?.id
            if (id != shopListIdFlow.value) {
                _errorInputName.value = true
//                   Log.d("validateInput", "_errorInputName.value = ${_errorInputName.value}")
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

    fun resetIntent() {
        _intent.value = null
    }

    fun setRenameListAppearance(show: Boolean) {
        _renameListAppearance.value = show
    }

    fun exportListToTxt() {
        scope.launch {
            _fileSaved.value = exportListToTxtUseCase(shopListIdFlow.value)
        }
    }

    fun shareTxtList()  {
        viewModelScope.launch {
            shareTxtListUseCase(shopListIdFlow.value).collect{
                _intent.value = it
            }
        }
    }


    fun undoDelete() {
        scope.launch {
            undoDeleteItemUseCase()
        }
    }


    fun resetFileSaved() {
        _fileSaved.value = null
    }


    companion object{
        private const val TAG = "ShopListViewModel"
    }
}