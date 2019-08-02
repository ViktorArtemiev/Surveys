package co.nimblehq.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import co.nimblehq.data.model.Survey
import co.nimblehq.data.source.survey.SurveyDataSource
import co.nimblehq.data.source.survey.SurveyDataSourceFactory
import javax.inject.Inject


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainViewModel @Inject constructor(private val sourceFactory: SurveyDataSourceFactory) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 5
    }

    val surveysLive: LiveData<PagedList<Survey>>
    val itemCountLive: LiveData<Int>
    val loadingLive: LiveData<Boolean>
    val errorLive: LiveData<Throwable>

    init {
        val pagedListConfig = buildPagedListConfig()
        surveysLive = LivePagedListBuilder(sourceFactory, pagedListConfig).build()
        itemCountLive = Transformations.switchMap<SurveyDataSource, Int>(
            sourceFactory.dataSourceLive, SurveyDataSource::itemCountLive)
        loadingLive = Transformations.switchMap<SurveyDataSource, Boolean>(
            sourceFactory.dataSourceLive, SurveyDataSource::loadingLive)
        errorLive = Transformations.switchMap<SurveyDataSource, Throwable>(
            sourceFactory.dataSourceLive, SurveyDataSource::errorLive)
    }

    private fun buildPagedListConfig(): PagedList.Config {
        return PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .setInitialLoadSizeHint(PAGE_SIZE * 2)
            .setEnablePlaceholders(false)
            .build()
    }

    fun refresh() {
        sourceFactory.dataSourceLive.value?.invalidate()
    }

    fun retry() {
        sourceFactory.dataSourceLive.value?.retry?.invoke()
    }
}