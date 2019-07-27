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
class SurveyDataSource(val surveysRepository: SurveysRepository) : PageKeyedDataSource<Int, Survey>() {

    var loadingLive = MutableLiveData<Boolean>()
    var errorLive = MutableLiveData<Throwable>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Survey>) {
        launch {
            val surveys = surveysRepository.getSurveys(page = 1, pageSize = params.requestedLoadSize)
            callback.onResult(surveys, null, 2)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        launch {
            val surveys = surveysRepository.getSurveys(page = params.key, pageSize = params.requestedLoadSize)
            callback.onResult(surveys, params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        // ignored
    }

    /**
     * Helper function to call a data load function with a loading and error handling.
     *
     * By marking `func` as `suspend` this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param func lambda to actually load data. It is called in the {@link coroutines.GlobalScope}.
     * Before calling the lambda the loading spinner will display, after completion or error the loading
     * spinner will stop.
     */
    private fun launch(func: suspend () -> Unit): Job {
        return GlobalScope.launch {
            try {
                loadingLive.value = true
                func()
            } catch (ex: Exception) {
                errorLive.value = ex
            } finally {
                loadingLive.value = false
            }
        }
    }

}