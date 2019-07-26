package co.nimblehq.screen.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.nimblehq.data.source.surveys.SurveysSource
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainViewModel @Inject constructor(val surveysSource: SurveysSource) : ViewModel() {

    val surveysLive = MutableLiveData<String>()
    val errorLive = MutableLiveData<Boolean>()

    fun fetchSurveys() {
        viewModelScope.launch {
            try {
                surveysLive.value = surveysSource.getSurveys()
            } catch (ex: Exception) {
                Timber.e(ex)
            }

        }
    }
}