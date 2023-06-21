package com.example.listadecompras.domain

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class AddShopItemUseCaseTest{

    private lateinit var addShopItem: AddShopItemUseCase
    private lateinit var fakeRepository: ShopListRepository

    @Before
    fun setup() {

        fakeRepository = FakeShopListRepository()
        addShopItem = AddShopItemUseCase(fakeRepository)

        val shopListToInsert = mutableListOf<ShopItem>()
        ('a'..'w').forEachIndexed { index, c ->
            shopListToInsert.add(
                ShopItem(
                    name = c.toString(),
                    count =  (1..5).random().toDouble(),
                    enabled = Random.nextBoolean()
                )
            )
        }
        shopListToInsert.shuffle()
        runBlocking {
            shopListToInsert.forEach{fakeRepository.addShopItem(it)}
        }
    }

    @Test
    fun `Add item, item added`() {

        runBlocking{

            val id = fakeRepository.getShopList().first().size
            val shopItem = ShopItem(
                name = ('x'..'z').random().toString(),
                count = 9.0,
                enabled = Random.nextBoolean(),
                id
            )
            addShopItem(shopItem)
            val newList = fakeRepository.getShopList().first()

            assertThat(newList.indexOf(shopItem)).isEqualTo(newList.size-1)
        }
    }
}