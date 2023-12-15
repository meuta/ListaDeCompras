package com.obrigada_eu.listadecompras.presentation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.obrigada_eu.listadecompras.R
import com.obrigada_eu.listadecompras.di.AppModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class ShopListEndToEndTest {

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
    fun saveItem_editAfterwards(){

        onView(ViewMatchers.withId(R.id.button_add_shop_item)).perform(click())

        onView(ViewMatchers.withId(R.id.et_name)).perform(typeText("test-name"))
        onView(ViewMatchers.withId(R.id.et_count)).perform(typeText("1"))
        onView(ViewMatchers.withId(R.id.btn_save)).perform(click())

        onView(withText("test-name")).check(matches(isDisplayed()))
        onView(withText("test-name")).perform(click())

        onView(ViewMatchers.withId(R.id.et_name)).check(matches(withText("test-name")))
        onView(ViewMatchers.withId(R.id.et_count)).check(matches(withText("1.01")))
        onView(ViewMatchers.withId(R.id.et_name)).perform(typeText("-2"))
        onView(ViewMatchers.withId(R.id.btn_save)).perform(click())

        onView(withText("test-name-2")).check(matches(isDisplayed()))
    }
}