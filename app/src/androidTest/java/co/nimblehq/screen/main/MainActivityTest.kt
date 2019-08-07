package co.nimblehq.screen.main

import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import co.nimblehq.R
import co.nimblehq.data.model.Survey
import co.nimblehq.rule.InjectableActivityIntentsTestRule
import co.nimblehq.screen.survey.SurveyActivity
import co.nimblehq.util.checkRecyclerViewItemCount
import co.nimblehq.util.onCurrentPage
import co.nimblehq.util.onViewPager
import co.nimblehq.util.waitFor
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

/**
 * Created by Viktor Artemiev on 2019-08-07.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainActivityTest {

    @get:Rule
    val activityRule = InjectableActivityIntentsTestRule(MainActivity::class.java) { activity ->
        activity.viewModelFactory = viewModelFactory
    }

    private val surveys = (1..5).map {
        Survey(
            id = it.toString(),
            title = "Title $it",
            description = "Description $it",
            imageUrl = "https://dhdbhh0jsld0o.cloudfront.net/m/$it"
        )
    }

    private val surveysLive = MutableLiveData<PagedList<Survey>>()
    private val itemCountLive = MutableLiveData<Int>()
    private val initialLoadLive = MutableLiveData<Boolean>()
    private val afterLoadLive = MutableLiveData<Boolean>()
    private val errorLive = MutableLiveData<Throwable>()

    private val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return viewModel as T
        }
    }

    lateinit var viewModel: MainViewModel
    lateinit var surveyPagedList: PagedList<Survey>

    @Before
    fun setup() {
        viewModel = mock {
            on { this.surveysLive } doReturn surveysLive
            on { this.itemCountLive } doReturn itemCountLive
            on { this.initialLoadLive } doReturn initialLoadLive
            on { this.afterLoadLive } doReturn afterLoadLive
            on { this.errorLive } doReturn errorLive
        }

        surveyPagedList = mockSurveyPagedList(surveys)
    }

    private fun mockSurveyPagedList(list: List<Survey>): PagedList<Survey> {
        val pagedList = mock(PagedList::class.java) as PagedList<Survey>
        `when`(pagedList[anyInt()]).then { invocation ->
            val index = invocation.arguments.first() as Int
            list[index]
        }
        `when`(pagedList.size).thenReturn(list.size)
        return pagedList
    }

    @Test
    fun launchActivity() {
        surveysLive.postValue(surveyPagedList)
        itemCountLive.postValue(surveyPagedList.size)
        initialLoadLive.postValue(false)
        activityRule.launchActivity(Intent())
        onView(allOf(withId(R.id.text_view_toolbar_title), isDisplayed())).check(matches(withText(R.string.activity_main_title)))
        onView(allOf(withContentDescription(R.string.activity_main_nav_btn_description), isClickable())).check(matches(isDisplayed()))
        onView(withId(R.id.view_pager)).check(matches(isDisplayed()))
        onView(withId(R.id.recycler_view_indicator)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.button_survey), isDisplayed(), isClickable())).check(matches(withText(R.string.activity_main_btn_survey)))
    }

    @Test
    fun viewPagerShowsSurveyItems() {
        surveysLive.postValue(surveyPagedList)
        activityRule.launchActivity(Intent())

        // check first item
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 1")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 1")

        // swipe to the third item
        repeat(2) {
            onViewPager().perform(swipeUp())
        }

        // check third item
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 3")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 3")

        // swipe to the second item
        onViewPager().perform(swipeDown())

        // check second item
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 2")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 2")

    }

    @Test
    fun viewPagerShowsHidesProgressBarItem() {
        initialLoadLive.postValue(true)
        activityRule.launchActivity(Intent())
        verifyCurrentPage(hasDescendant(allOf(withId(R.id.progress_bar), isDisplayed())))
        initialLoadLive.postValue(false)
        surveysLive.postValue(surveyPagedList)
        onView(isRoot()).perform(waitFor(1000))
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 1")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 1")
    }

    @Test
    fun viewPagerShowsHidesErrorItem() {
        errorLive.postValue(Error("Some error"))
        activityRule.launchActivity(Intent())
        verifyCurrentPage(hasDescendant(allOf(withText(R.string.item_error_tv_message), isDisplayed())))
        verifyCurrentPage(hasDescendant(allOf(withId(R.id.button_retry), withText(R.string.item_error_btn_retry), isDisplayed())))
        doAnswer {
            initialLoadLive.postValue(false)
            surveysLive.postValue(surveyPagedList)
        }.`when`(viewModel).retry()
        onView(withId(R.id.button_retry)).perform(click())
        verify(viewModel).retry()
        onView(isRoot()).perform(waitFor(1000))
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 1")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 1")
    }

    @Test
    fun clickReloadButton() {
        surveysLive.postValue(surveyPagedList)
        activityRule.launchActivity(Intent())

        doAnswer {
            initialLoadLive.postValue(false)
            surveysLive.postValue(surveyPagedList)
        }.`when`(viewModel).reload()

        // can be thrown PerformException 'Animations or transitions are enabled on the target device.'
        // fix https://stackoverflow.com/questions/44005338/android-espresso-performexception
        onView(withContentDescription(R.string.activity_main_nav_btn_description)).perform(click())
        verify(viewModel).reload()
        onView(isRoot()).perform(waitFor(1000))
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 1")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 1")
    }

    @Test
    fun recyclerViewIndicatorShowsCorrectItems() {
        surveysLive.postValue(surveyPagedList)
        itemCountLive.postValue(surveyPagedList.size)
        activityRule.launchActivity(Intent())

        val recyclerView = withId(R.id.recycler_view_indicator)

        // check indicators count
        onView(recyclerView).check(checkRecyclerViewItemCount(5))

        // check first indicator is selected
        onView(allOf(withParent(recyclerView), withParentIndex(0))).check(matches(isEnabled()))
        onView(allOf(withParent(recyclerView), withParentIndex(1))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(2))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(3))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(4))).check(matches(Matchers.not(isEnabled())))

        // swipe to the third item
        repeat(2) {
            onViewPager().perform(swipeUp())
        }

        // check third indicator is selected
        onView(allOf(withParent(recyclerView), withParentIndex(0))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(1))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(2))).check(matches(isEnabled()))
        onView(allOf(withParent(recyclerView), withParentIndex(3))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(4))).check(matches(Matchers.not(isEnabled())))

        onViewPager().perform(swipeDown())

        // check second indicator is selected
        onView(allOf(withParent(recyclerView), withParentIndex(0))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(1))).check(matches(isEnabled()))
        onView(allOf(withParent(recyclerView), withParentIndex(2))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(3))).check(matches(Matchers.not(isEnabled())))
        onView(allOf(withParent(recyclerView), withParentIndex(4))).check(matches(Matchers.not(isEnabled())))

    }

    @Test
    fun changeOrientation() {
        surveysLive.postValue(surveyPagedList)
        itemCountLive.postValue(surveyPagedList.size)
        activityRule.launchActivity(Intent())

        // swipe to the third item
        repeat(2) {
            onViewPager().perform(swipeUp())
        }

        // check third indicator is selected
        onView(allOf(withParent(withId(R.id.recycler_view_indicator)), withParentIndex(2))).check(matches(isEnabled()))

        // check third item
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 3")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 3")

        // change orientation to landscape
        activityRule.activity.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE

        // check third indicator is selected
        onView(allOf(withParent(withId(R.id.recycler_view_indicator)), withParentIndex(2))).check(matches(isEnabled()))

        // check third item
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 3")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 3")

        // swipe to the fifth item
        repeat(2) {
            onViewPager().perform(swipeUp())
        }

        // check fifth indicator is selected
        onView(allOf(withParent(withId(R.id.recycler_view_indicator)), withParentIndex(4))).check(matches(isEnabled()))

        // check fifth item
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 5")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 5")

        // change orientation back to portrait
        activityRule.activity.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT

        // check fifth indicator is selected
        onView(allOf(withParent(withId(R.id.recycler_view_indicator)), withParentIndex(4))).check(matches(isEnabled()))

        // check fifth item
        verifyCurrentPageViewWithText(R.id.text_view_title, "Title 5")
        verifyCurrentPageViewWithText(R.id.text_view_description, "Description 5")
    }

    private fun verifyCurrentPageViewWithText(@IdRes id: Int, text: String) {
        verifyCurrentPage(
            hasDescendant(
                allOf(withId(id), withText(text), isDisplayed())
            )
        )
    }

    private fun verifyCurrentPage(matcher: Matcher<View>) {
        onCurrentPage().check(matches(matcher))
    }

    @Test
    fun clickSurveyButton() {
        surveysLive.postValue(surveyPagedList)
        initialLoadLive.postValue(false)
        activityRule.launchActivity(Intent())
        onView(withId(R.id.button_survey)).perform(click())
        intended(
            allOf(hasComponent(SurveyActivity::class.java.name),
                hasExtra(SurveyActivity.EXTRA_SURVEY, surveyPagedList.first()))
        )
    }
}
