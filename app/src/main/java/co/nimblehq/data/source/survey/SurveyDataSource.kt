package co.nimblehq.data.source.survey

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import co.nimblehq.data.model.Survey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

/**
 * Data loader for page-keyed [Survey] content, where requests return keys for next/previous
 * pages.
 *
 * [Int] Type of data used to query Value types out of the DataSource.
 * [Survey] Type of items being loaded by the DataSource.
 *
 * @param surveyRepository Data Repository that loads [Survey] content from network
 */
class SurveyDataSource(private val surveyRepository: SurveyRepository,
                       private val dispatcher: CoroutineDispatcher) : PageKeyedDataSource<Int, Survey>() {

    var retry: (() -> Any)? = null

    val itemCountLive = MutableLiveData<Int>()
    val initialLoadingLive = MutableLiveData<Boolean>()
    val afterLoadingLive = MutableLiveData<Boolean>()
    val errorLive = MutableLiveData<Throwable>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Survey>) {
        GlobalScope.launch(dispatcher) {
            try {
                initialLoadingLive.postValue(true)
                val surveys = surveyRepository.getSurveys(page = 1, pageSize = params.requestedLoadSize)
                initialLoadingLive.postValue(false)
                callback.onResult(surveys, null, 2)
                itemCountLive.postValue(calcTotalItemCount(surveys.size))
                retry = null
            } catch (error: Throwable) {
                retry = { loadInitial(params, callback) }
                errorLive.postValue(error)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        GlobalScope.launch(dispatcher) {
            try {
                afterLoadingLive.postValue(true)
                val surveys = surveyRepository.getSurveys(page = params.key, pageSize = params.requestedLoadSize)
                afterLoadingLive.postValue(false)
                callback.onResult(surveys, params.key + 1)
                itemCountLive.postValue(calcTotalItemCount(surveys.size))
                retry = null
            } catch (error: Throwable) {
                retry = { loadAfter(params, callback) }
                errorLive.postValue(error)
            }
        }
    }

    private fun calcTotalItemCount(surveysSize: Int): Int {
        return if (itemCountLive.value == null) {
            surveysSize
        } else {
            itemCountLive.value!! + surveysSize
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        // ignored
    }
}