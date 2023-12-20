package com.obrigada_eu.listadecompras.domain

import javax.inject.Inject

class SetCurrentListIdUseCase @Inject constructor(private val repository: ShopListRepository) {

    suspend operator fun invoke(listId: Int) = repository.setCurrentListId(listId)
}