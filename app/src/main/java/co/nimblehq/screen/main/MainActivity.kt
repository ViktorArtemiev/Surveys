package co.nimblehq.screen.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import co.nimblehq.R
import co.nimblehq.di.Injectable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Viktor Artemiev on 2019-07-25.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainActivity : AppCompatActivity(), Injectable {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.inflateMenu(R.menu.menu_main)

        viewModel = ViewModelProviders.of(this@MainActivity, viewModelFactory).get()
        viewModel.surveysLive.observe(this@MainActivity, Observer { Timber.d(it) })
        viewModel.fetchSurveys()
    }

}
