package com.obrigada_eu.listadecompras

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.obrigada_eu.listadecompras.di.AppModule
import com.obrigada_eu.listadecompras.presentation.shop_list.ShopListActivity
import com.obrigada_eu.listadecompras.presentation.shop_item.ShopItemViewHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class ShopListWithItemsActivityViewModelTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityScenarioRule = ActivityScenarioRule(ShopListActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
        activityScenarioRule.scenario
    }


    @Test
    fun isFabDisplayed(){
        onView(withId(R.id.button_add_shop_item)).check(matches(isDisplayed()))
    }

    @Test
    fun isListDisplayed(){
        onView(withId(R.id.rv_shop_list)).check(matches(isDisplayed()))
    }

    @Test
    fun isListScrolling(){
        onView(withId(R.id.rv_shop_list)).perform(scrollToLastPosition<ShopItemViewHolder>())
    }
}