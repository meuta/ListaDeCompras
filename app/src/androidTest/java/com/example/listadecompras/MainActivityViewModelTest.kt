package com.example.listadecompras

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.listadecompras.di.AppModule
import com.example.listadecompras.presentation.MainActivity
import com.example.listadecompras.presentation.old_staff.ShopItemViewHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class MainActivityViewModelTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

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