package com.sopnolikhi.booksmyfriend

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sopnolikhi.booksmyfriend.design.ui.activities.WelcomeActivity
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Walks a new reader through the whole app: onboarding, naming, browsing, reading and settings.
 * Screenshots of each screen are saved for design review (pulled by CI).
 */
@RunWith(AndroidJUnit4::class)
class AppFlowTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun freshInstall() {
        listOf("IntroLayout", "UserLoginPref", "LibraryPref", "SettingsPref").forEach {
            context.getSharedPreferences(it, Context.MODE_PRIVATE).edit().clear().commit()
        }
    }

    @Test
    fun newReaderCanOnboardBrowseAndRead() {
        ActivityScenario.launch(WelcomeActivity::class.java).use {
            // Onboarding
            onView(withText(R.string.onboardingTitle1)).check(matches(isDisplayed()))
            screenshot("01_onboarding")
            onView(withId(R.id.introNextButton)).perform(click())
            onView(withId(R.id.introNextButton)).perform(click())
            screenshot("02_onboarding_last")
            onView(withId(R.id.introNextButton)).perform(click())

            // Name
            onView(withText(R.string.authTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.startReadingButton)).perform(click())
            onView(withText(R.string.nameTooShortText)).check(matches(isDisplayed()))
            onView(withId(R.id.nameInput)).perform(replaceText("Toufik"), closeSoftKeyboard())
            screenshot("03_name")
            onView(withId(R.id.startReadingButton)).perform(click())

            // Home
            onView(withId(R.id.nameText)).check(matches(withText("Toufik")))
            screenshot("04_home")

            // Book details, save it, and start reading
            onView(allOf(withText("Pride and Prejudice"), isDescendantOfA(withId(R.id.popularList)))).perform(click())
            onView(withId(R.id.detailTitle)).check(matches(withText("Pride and Prejudice")))
            onView(withId(R.id.saveButton)).perform(click())
            screenshot("05_details")
            onView(withId(R.id.readButton)).perform(click())

            // Reader
            onView(withId(R.id.readerChapter)).check(matches(withText("Chapter 1")))
            screenshot("06_reader")
            onView(withId(R.id.readerSettings)).perform(click())
            onView(withId(R.id.pageSepiaButton)).perform(click())
            screenshot("07_reader_settings")
            pressBack()
            screenshot("08_reader_sepia")
            pressBack()
            pressBack()

            // Home now offers to continue
            onView(withId(R.id.continueCard)).check(matches(isDisplayed()))
            screenshot("09_home_continue")

            // Explore
            onView(withId(R.id.navigationExplore)).perform(click())
            onView(withId(R.id.exploreGrid)).check(matches(isDisplayed()))
            closeKeyboard()
            screenshot("10_explore")
            onView(withId(R.id.searchInput)).perform(replaceText("zzzz"), closeSoftKeyboard())
            onView(withText(R.string.noResultsTitle)).check(matches(isDisplayed()))
            onView(withId(R.id.searchInput)).perform(replaceText(""), closeSoftKeyboard())

            // Library shows the book in progress and the saved book
            onView(withId(R.id.navigationLibrary)).perform(click())
            onView(allOf(withText("Pride and Prejudice"), isDescendantOfA(withId(R.id.libraryList))))
                .check(matches(isDisplayed()))
            screenshot("11_library")
            onView(withId(R.id.savedTabButton)).perform(click())
            onView(allOf(withText("Pride and Prejudice"), isDescendantOfA(withId(R.id.libraryList))))
                .check(matches(isDisplayed()))

            // Profile
            onView(withId(R.id.navigationProfile)).perform(click())
            onView(withId(R.id.profileName)).check(matches(withText("Toufik")))
            screenshot("12_profile")

            // Dark theme
            onView(withId(R.id.themeNightButton)).perform(click())
            onView(withId(R.id.profileScroll)).perform(swipeUp())
            screenshot("13_profile_dark")
            onView(withId(R.id.navigationHome)).perform(click())
            screenshot("14_home_dark")
        }
    }

    private fun closeKeyboard() {
        onView(withId(R.id.searchInput)).perform(closeSoftKeyboard())
    }

    private fun screenshot(name: String) {
        onIdle()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Thread.sleep(400)
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot() ?: return
        val dir = File(context.getExternalFilesDir(null), "screenshots").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
