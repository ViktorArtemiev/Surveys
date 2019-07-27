package co.nimblehq.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import co.nimblehq.data.model.Survey
import co.nimblehq.data.source.surveys.SurveyDataSource
import co.nimblehq.data.source.surveys.SurveysRepository
import javax.inject.Inject


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainViewModel @Inject constructor(val surveysRepository: SurveysRepository) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 10
    }

    val surveysLive: LiveData<PagedList<Survey>>
    val errorLive = MutableLiveData<Boolean>()

    init {
        val pagedListConfig = buildPagedListConfig()
        surveysLive = buildLivePagedList(pagedListConfig)
    }

    private fun buildPagedListConfig(): PagedList.Config {
        return PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE * 2)
            .setEnablePlaceholders(false)
            .build()
    }

    private fun buildLivePagedList(config: PagedList.Config): LiveData<PagedList<Survey>> {
        val dataSourceFactory = object : DataSource.Factory<Int, Survey>() {
            override fun create(): DataSource<Int, Survey> {
                return SurveyDataSource(surveysRepository)
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config).build()
    }
}