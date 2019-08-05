package co.nimblehq.screen.main

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.paging.PagedList
import androidx.viewpager2.widget.ViewPager2
import co.nimblehq.R
import co.nimblehq.data.model.Survey
import co.nimblehq.screen.main.adapter.IndicatorsAdapter
import co.nimblehq.screen.main.adapter.SurveysAdapter
import co.nimblehq.screen.survey.SurveyActivity
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Viktor Artemiev on 2019-07-25.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainActivity : DaggerAppCompatActivity() {

    private val surveysAdapter = SurveysAdapter { retrySurveys() }
    private val indicatorsAdapter = IndicatorsAdapter()

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            indicatorsAdapter.selectItemByPosition(position)
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setNavigationOnClickListener { refreshSurveys() }

        view_pager.orientation = ViewPager2.ORIENTATION_VERTICAL
        view_pager.adapter = surveysAdapter
        view_pager.registerOnPageChangeCallback(onPageChangeCallback)

        recycler_view_indicator.adapter = indicatorsAdapter

        button_survey.setOnClickListener { getSelectedSurvey()?.let { startSurveyActivity(it) } }

        viewModel = provideViewModel()
    }

    fun retrySurveys() {
        viewModel.retry()
    }

    fun refreshSurveys() {
        viewModel.refresh()
        indicatorsAdapter.clear()
    }

    fun getSelectedSurvey() = surveysAdapter.getItem(view_pager.currentItem)

    fun startSurveyActivity(survey: Survey) {
        val intent = SurveyActivity.startIntent(this@MainActivity, survey)
        startActivity(intent)
    }

    fun provideViewModel() = ViewModelProviders.of(this@MainActivity, viewModelFactory)
        .get<MainViewModel>().apply {
            surveysLive.observe(this@MainActivity, Observer { handleSurveys(it) })
            itemCountLive.observe(this@MainActivity, Observer { handleItemCount(it) })
            initialLoadingLive.observe(this@MainActivity, Observer { handleInitialLoading(it) })
            afterLoadingLive.observe(this@MainActivity, Observer { handleAfterLoading(it) })
            errorLive.observe(this@MainActivity, Observer { handleError(it) })
        }

    fun handleSurveys(surveys: PagedList<Survey>) {
        surveysAdapter.submitList(surveys)
    }

    fun handleItemCount(count: Int) {
        indicatorsAdapter.itemCount = count
        indicatorsAdapter.selectItemByPosition(view_pager.currentItem)
    }

    fun handleInitialLoading(isLoading: Boolean) {
        button_survey.isVisible = !isLoading
        handleAfterLoading(isLoading)
    }

    fun handleAfterLoading(isLoading: Boolean) {
        surveysAdapter.setState(if (isLoading) SurveysAdapter.State.LOADING else SurveysAdapter.State.DONE)
    }

    fun handleError(error: Throwable) {
        Timber.e(error)
        surveysAdapter.setState(SurveysAdapter.State.ERROR)
    }

}
