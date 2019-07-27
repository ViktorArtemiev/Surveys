package co.nimblehq.screen.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import androidx.paging.PagedList
import androidx.viewpager2.widget.ViewPager2
import co.nimblehq.R
import co.nimblehq.data.model.Survey
import co.nimblehq.di.Injectable
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * Created by Viktor Artemiev on 2019-07-25.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainActivity : AppCompatActivity(), Injectable {

    val surveysAdapter = SurveysAdapter()

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setNavigationOnClickListener { refreshSurveys() }

        view_pager.orientation = ViewPager2.ORIENTATION_VERTICAL
        view_pager.adapter = surveysAdapter

        viewModel = ViewModelProviders.of(this@MainActivity, viewModelFactory).get()
        viewModel.surveysLive.observe(this@MainActivity, Observer { handleSurveys(it) })
    }

    fun handleSurveys(surveys: PagedList<Survey>) {
        surveysAdapter.submitList(surveys)
    }

    fun refreshSurveys() {
        viewModel.refresh()
    }

    fun retrySurveys() {

    }

}
