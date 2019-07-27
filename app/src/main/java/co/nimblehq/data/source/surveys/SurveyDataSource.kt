package co.nimblehq.data.source.surveys

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import co.nimblehq.data.model.Survey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveyDataSource(private val surveysRepository: SurveysRepository) : PageKeyedDataSource<Int, Survey>() {

    var retry: (() -> Any)? = null

    val itemCountLive = MutableLiveData<Int>()
    val loadingLive = MutableLiveData<Boolean>()
    val errorLive = MutableLiveData<Throwable>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Survey>) {
        GlobalScope.launch {
            try {
                loadingLive.postValue(true)
                val surveys = surveysRepository.getSurveys(page = 1, pageSize = params.requestedLoadSize)
                loadingLive.postValue(false)
                retry = null
                callback.onResult(surveys, null, 2)
                itemCountLive.postValue(calcTotalItemCount(surveys.size))
            } catch (error: Throwable) {
                retry = { loadInitial(params, callback) }
                errorLive.postValue(error)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Survey>) {
        GlobalScope.launch {
            try {
                loadingLive.postValue(true)
                val surveys = surveysRepository.getSurveys(page = params.key, pageSize = params.requestedLoadSize)
                loadingLive.postValue(false)
                retry = null
                callback.onResult(surveys, params.key + 1)
                itemCountLive.postValue(calcTotalItemCount(surveys.size))
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