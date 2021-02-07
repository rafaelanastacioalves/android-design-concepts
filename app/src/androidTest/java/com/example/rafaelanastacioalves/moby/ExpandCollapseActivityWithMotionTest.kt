package com.example.rafaelanastacioalves.moby


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.rafaelanastacioalves.moby.util.HelperMethods.withHolderContainingId
import com.example.rafaelanastacioalves.moby.util.RestServiceTestHelper
import com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation.ExpandCollapseActivityWithMotion
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class ExpandCollapseActivityWithMotionTest {

    @get:Rule
    var mainActivityActivityTestRule = ActivityTestRule(ExpandCollapseActivityWithMotion::class.java, true, false)
    private val fileNameMainEntityListOKResponse = "main_entity_ok_response.json"
    private var server: MockWebServer? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        server = MockWebServer()
        server!!.start(1234)
        InstrumentationRegistry.registerInstance(InstrumentationRegistry.getInstrumentation(), Bundle())
        server!!.url("/").toString()


    }

    @Test
    @Throws(IOException::class)
    fun shouldEntityListSuccess() {
        server!!.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(
                        InstrumentationRegistry.getInstrumentation().context, fileNameMainEntityListOKResponse)
                )
        )

        val intent = Intent()

        mainActivityActivityTestRule.launchActivity(intent)

        onView(
                withId(R.id.main_entity_list)
        ).perform(
                RecyclerViewActions.scrollToHolder(
                        withHolderContainingId(R.id.entity_detail_title_textview)
                )
        )
        onView(allOf<View>(withId(R.id.entity_detail_title_textview), withText("Disney Premium"))).check(matches(isDisplayed()))

    }


    @After
    @Throws(Exception::class)
    fun tearDown() {
        server!!.shutdown()
    }
}
