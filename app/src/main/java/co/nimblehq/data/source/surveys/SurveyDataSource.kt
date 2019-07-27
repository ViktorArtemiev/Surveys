package co.nimblehq.data.source.surveys

import androidx.paging.PageKeyedDataSource
import co.nimblehq.data.model.Survey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveyDataSource(val surveysRepository: SurveysRepository) : PageKeyedDataSource<Int, Survey>() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Survey>) {
        GlobalScope.launch {
            val surveys = surveysRepository.getSurveys(page = 1, pageSize = params.requestedLoadSize)
            callback.onResult(surveys, null, 2)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        GlobalScope.launch {
            val surveys = surveysRepository.getSurveys(page = params.key, pageSize = params.requestedLoadSize)
            callback.onResult(surveys, params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        // ignored
    }

}