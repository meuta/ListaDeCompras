package com.example.listadecompras

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToLastPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.listadecompras.di.TestAppModule
import com.example.listadecompras.presentation.MainActivity
import com.example.listadecompras.presentation.ShopItemViewHolder
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(TestAppModule::class)
class MainActivityViewModelTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }


    @Test
    fun isFabDisplayed(){
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.button_add_shop_item)).check(matches(isDisplayed()))
    }

    @Test
    fun isListDisplayed(){
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.rv_shop_list)).check(matches(isDisplayed()))
    }

    @Test
    fun isListScrolling(){
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.rv_shop_list)).perform(scrollToLastPosition<ShopItemViewHolder>())
    }
}