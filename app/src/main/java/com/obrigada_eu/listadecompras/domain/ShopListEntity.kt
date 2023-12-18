package com.obrigada_eu.listadecompras.domain

data class ShopListEntity(
    val name: String,
    val id: Int = UNDEFINED_ID
)
{
    companion object{
        const val UNDEFINED_ID = 0
    }
}