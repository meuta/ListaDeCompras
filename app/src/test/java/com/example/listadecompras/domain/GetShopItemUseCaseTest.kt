package com.example.listadecompras.domain

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetShopItemUseCaseTest{

    private lateinit var getShopItem: GetShopItemUseCase
    private lateinit var fakeRepository: FakeShopListRepository

    @Before
    fun setUp() {
        fakeRepository = FakeShopListRepository()
        getShopItem = GetShopItemUseCase(fakeRepository)

        val shopListToInsert = mutableListOf<ShopItem>()
        ('a'..'z').forEachIndexed { index, c ->
            shopListToInsert.add(
                ShopItem(
                    name = c.toString(),
                    count = 1.0,
                    enabled = true
                )
            )
        }
//        shopListToInsert.shuffle()
        runBlocking {
            shopListToInsert.forEach{fakeRepository.addShopItem(it)}
        }
    }

    @Test
    fun `Get required item, item correct`() = runBlocking{

        for ((index, i) in ('a'..'z').withIndex()) {
            val shopItem = getShopItem(index)
            assertThat(shopItem.name).isEqualTo(i.toString())
        }
    }

}