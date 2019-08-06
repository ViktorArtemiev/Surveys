package co.nimblehq.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import co.nimblehq.data.model.Survey
import co.nimblehq.data.source.survey.SurveyDataSource
import co.nimblehq.data.source.survey.SurveyDataSourceFactory
import java.util.concurrent.Executor
import javax.inject.Inject


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainViewModel @Inject constructor(private val sourceFactory: SurveyDataSourceFactory) : ViewModel() {

    companion object {
        const val PAGE_SIZE = 5
    }

    val surveysLive: LiveData<PagedList<Survey>>
    val itemCountLive: LiveData<Int>
    val initialLoadLive: LiveData<Boolean>
    val afterLoadLive: LiveData<Boolean>
    val errorLive: LiveData<Throwable>

    init {
        surveysLive = sourceFactory.toLiveData(
            config = Config(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSizeHint = PAGE_SIZE * 2
            ),
            fetchExecutor = Executor { command -> command.run() }

        )
        itemCountLive = Transformations.switchMap<SurveyDataSource, Int>(
            sourceFactory.dataSourceLive, SurveyDataSource::itemCountLive
        )
        initialLoadLive = Transformations.switchMap<SurveyDataSource, Boolean>(
            sourceFactory.dataSourceLive, SurveyDataSource::initialLoadingLive
        )
        afterLoadLive = Transformations.switchMap<SurveyDataSource, Boolean>(
            sourceFactory.dataSourceLive, SurveyDataSource::afterLoadingLive
        )
        errorLive = Transformations.switchMap<SurveyDataSource, Throwable>(
            sourceFactory.dataSourceLive, SurveyDataSource::errorLive
        )
    }

    fun refresh() {
        sourceFactory.dataSourceLive.value?.invalidate()
    }

    fun retry() {
        sourceFactory.dataSourceLive.value?.retry?.invoke()
    }
}