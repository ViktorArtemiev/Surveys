package co.nimblehq.screen.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.paging.PagedList
import androidx.viewpager2.widget.ViewPager2
import co.nimblehq.R
import co.nimblehq.data.model.Survey
import co.nimblehq.di.Injectable
import co.nimblehq.screen.main.adapter.IndicatorsAdapter
import co.nimblehq.screen.main.adapter.SurveysAdapter
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Viktor Artemiev on 2019-07-25.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainActivity : AppCompatActivity(), Injectable {

    private val surveysAdapter = SurveysAdapter { retrySurveys() }
    private val indicatorsAdapter = IndicatorsAdapter()

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            survey = surveysAdapter.getItem(position)
            button_survey.isVisible = survey != null
            indicatorsAdapter.selectItemByPosition(position)
        }
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    var survey: Survey? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setNavigationOnClickListener { refreshSurveys() }

        view_pager.orientation = ViewPager2.ORIENTATION_VERTICAL
        view_pager.adapter = surveysAdapter
        view_pager.registerOnPageChangeCallback(onPageChangeCallback)

        recycler_view_indicator.adapter = indicatorsAdapter

        viewModel = ViewModelProviders.of(this@MainActivity, viewModelFactory).get()
        viewModel.surveysLive.observe(this@MainActivity, Observer { handleSurveys(it) })
        viewModel.itemCountLive.observe(this@MainActivity, Observer { handleCount(it) })
        viewModel.loadingLive.observe(this@MainActivity, Observer { handleLoading(it) })
        viewModel.errorLive.observe(this@MainActivity, Observer { handleError(it) })
    }

    fun retrySurveys() {
        viewModel.retry()
    }

    fun refreshSurveys() {
        viewModel.refresh()
        indicatorsAdapter.clear()
    }

    fun handleSurveys(surveys: PagedList<Survey>) {
        surveysAdapter.submitList(surveys)
    }

    fun handleCount(count: Int) {
        indicatorsAdapter.itemCount = count
    }

    fun handleLoading(isLoading: Boolean) {
        button_survey.isVisible = !isLoading
        if (isLoading) surveysAdapter.setState(SurveysAdapter.State.LOADING)
        else surveysAdapter.setState(SurveysAdapter.State.DONE)
    }

    fun handleError(error: Throwable) {
        Timber.e(error)
        surveysAdapter.setState(SurveysAdapter.State.ERROR)
    }

}
