package co.nimblehq.data.source.surveys

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import co.nimblehq.data.model.Survey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveyDataSource(private val surveysRepository: SurveysRepository) : PageKeyedDataSource<Int, Survey>() {

    var loadingLive = MutableLiveData<Boolean>()
    var errorLive = MutableLiveData<Throwable>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Survey>) {
        launch {
            loadingLive.postValue(true)
            val surveys = surveysRepository.getSurveys(page = 1, pageSize = params.requestedLoadSize)
            loadingLive.postValue(false)
            callback.onResult(surveys, null, 2)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        launch {
            loadingLive.postValue(true)
            val surveys = surveysRepository.getSurveys(page = params.key, pageSize = params.requestedLoadSize)
            loadingLive.postValue(false)
            callback.onResult(surveys, params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        // ignored
    }

    /**
     * Helper function to call a data load function with an error handling.
     *
     * By marking `func` as `suspend` this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param func lambda to actually load data. It is called in the {@link coroutines.GlobalScope}.
     */
    private fun launch(func: suspend () -> Unit): Job {
        return GlobalScope.launch {
            try {
                func()
            } catch (ex: Exception) {
                errorLive.postValue(ex)
            }
        }
    }
}