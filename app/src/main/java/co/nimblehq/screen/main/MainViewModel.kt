package co.nimblehq.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import co.nimblehq.data.model.Survey
import co.nimblehq.data.source.surveys.SurveyDataSource
import co.nimblehq.data.source.surveys.SurveysDataSourceFactory
import co.nimblehq.data.source.surveys.SurveysRepository
import javax.inject.Inject


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainViewModel @Inject constructor(surveysRepository: SurveysRepository) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 5
    }

    private val dataSourceFactory = SurveysDataSourceFactory(surveysRepository)

    val surveysLive: LiveData<PagedList<Survey>>
    val itemCountLive: LiveData<Int>
    val loadingLive: LiveData<Boolean>
    val errorLive: LiveData<Throwable>

    init {
        val pagedListConfig = buildPagedListConfig()
        surveysLive = LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
        itemCountLive = Transformations.switchMap<SurveyDataSource, Int>(
            dataSourceFactory.dataSourceLive, SurveyDataSource::itemCountLive)
        loadingLive = Transformations.switchMap<SurveyDataSource, Boolean>(
            dataSourceFactory.dataSourceLive, SurveyDataSource::loadingLive)
        errorLive = Transformations.switchMap<SurveyDataSource, Throwable>(
            dataSourceFactory.dataSourceLive, SurveyDataSource::errorLive)
    }

    private fun buildPagedListConfig(): PagedList.Config {
        return PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE * 2)
            .setEnablePlaceholders(false)
            .build()
    }

    fun refresh() {
        dataSourceFactory.dataSourceLive.value?.invalidate()
    }

    fun retry() {
        dataSourceFactory.dataSourceLive.value?.retry?.invoke()
    }
}