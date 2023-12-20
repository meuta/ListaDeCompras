package com.obrigada_eu.listadecompras.domain

import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCurrentListIdUseCase @Inject constructor(private val repository: ShopListRepository) {

    operator fun invoke(): StateFlow<Int> = repository.getCurrentListId()
}