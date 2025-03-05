package com.obrigada_eu.listadecompras.presentation.list_set

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.domain.shop_list.AddShopListUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.GetAllListsWithoutItemsUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.SaveListToDbUseCase
import com.obrigada_eu.listadecompras.domain.shop_list.ShopListWithItems
import com.obrigada_eu.listadecompras.presentation.list_set.NewListCreationFragment.Companion.NAME_FROM_CONTENT_FIELD
import com.obrigada_eu.listadecompras.presentation.list_set.NewListCreationFragment.Companion.NAME_FROM_TITLE_FIELD
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FragmentNewListViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val getAllListsWithoutItemsUseCase: GetAllListsWithoutItemsUseCase,
    private val addShopListUseCase: AddShopListUseCase,
    private val saveListToDbUseCase: SaveListToDbUseCase,
) : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val getStringResource: (Int) -> String  = { id -> context.getString(id) }

    private var fromTxtFile = false

    private var _resetListFragmentUI = MutableSharedFlow<Unit>()
    val resetListFragmentUI: SharedFlow<Unit> = _resetListFragmentUI

    private fun resetListFragmentUI() {
        scope.launch { _resetListFragmentUI.emit(Unit) }
    }

    private var oldListName: String? = null

    private val _isAlternativeName = MutableStateFlow<Boolean>(false)
    val isAlternativeName: StateFlow<Boolean> = _isAlternativeName

    private val _isNameFromTitle = MutableStateFlow<Boolean>(true)
    val isNameFromTitle: StateFlow<Boolean> = _isNameFromTitle

    private val _errorInputNameFromTitle: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorInputNameFromTitle: StateFlow<String?> = _errorInputNameFromTitle

    private val _errorInputNameFromContent: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorInputNameFromContent: StateFlow<String?> = _errorInputNameFromContent

    private val _listSaved: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val listSaved: StateFlow<Boolean?> = _listSaved


    fun updateUiState(fromTxtFile: Boolean, nameFromTitle: String, nameFromContent: String?) {
        this.fromTxtFile = fromTxtFile
        oldListName = nameFromTitle

        scope.launch { if (fromTxtFile) validateInput(nameFromTitle, NAME_FROM_TITLE_FIELD) }

        if (nameFromContent != null && nameFromContent != nameFromTitle) {
            _isAlternativeName.value = true
            scope.launch { validateInput(nameFromContent, NAME_FROM_CONTENT_FIELD) }
        }
    }



    fun addShopList(
        name: String,
        shopList: ShopListWithItems?,
        alterName: String? = null // editText content
    ) {

        viewModelScope.launch {
            val fieldIsValid = validateInput(name, NAME_FROM_TITLE_FIELD)
            if (!fromTxtFile) {
                // new empty list:

                if (fieldIsValid) {
                    _listSaved.value = addShopListUseCase(name)
                    resetListFragmentUI()
                }
            } else {
                // new list from txt file:

                shopList?.let { list ->

                    if (!isAlternativeName.value) {
                        if (fieldIsValid) {
                            // names from title and content are equals, name is valid:

                            _listSaved.value = saveListToDbUseCase(list.copy(name = name))
                            resetListFragmentUI()
                        }

                    } else {
                        // names from title and content are different
                        val listNameFromContent = alterName ?: ""

                        val fieldIsValidContent = validateInput(listNameFromContent, NAME_FROM_CONTENT_FIELD)

                        if (fieldIsValidContent && !isNameFromTitle.value) {
                            // name is from title and is valid:
                            _listSaved.value = saveListToDbUseCase(list.copy(name = listNameFromContent))
                            resetListFragmentUI()
                        } else if (fieldIsValid && isNameFromTitle.value) {
                            // name is from content and is valid:
                            _listSaved.value = saveListToDbUseCase(list.copy(name = name))
                            resetListFragmentUI()
                        }
                    }
                }
            }
        }
    }


    private suspend fun validateInput(name: String, field: String): Boolean {

        if (name.isBlank()) {
            val errorText = getStringResource(R.string.error_input_list_name_empty)
            when (field) {
                NAME_FROM_TITLE_FIELD -> _errorInputNameFromTitle.value = errorText
                NAME_FROM_CONTENT_FIELD -> _errorInputNameFromContent.value = errorText
            }
            return false
        }

        val myNamesList = getAllListsWithoutItemsUseCase().map { it.name }
        if (myNamesList.contains(name)) {
            val errorText = getStringResource(R.string.error_input_list_name_duplicated)

            when (field) {
                NAME_FROM_TITLE_FIELD -> _errorInputNameFromTitle.value = errorText
                NAME_FROM_CONTENT_FIELD -> _errorInputNameFromContent.value = errorText
            }
            return false
        }
        return true
    }



    fun resetErrorInputName(field: String) {
        when(field){
            NAME_FROM_TITLE_FIELD -> _errorInputNameFromTitle.value = null
            NAME_FROM_CONTENT_FIELD -> _errorInputNameFromContent.value = null
        }
    }

    fun setIsNameFromTitle(isFromTitle: Boolean) {
        _isNameFromTitle.value = isFromTitle
    }


    companion object {

        private const val TAG = "FragmentNewListViewModel"
    }
}