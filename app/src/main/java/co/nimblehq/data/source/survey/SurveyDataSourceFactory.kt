package co.nimblehq.data.source.survey

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import co.nimblehq.data.model.Survey


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

/**
 * Factory for [Survey] DataSources implements [DataSource.Factory].
 *
 * This class allows {@code LiveData<SurveyDataSource>} to be created.
 */
class SurveyDataSourceFactory(private val surveyRepository: SurveyRepository) : DataSource.Factory<Int, Survey>() {

    val dataSourceLive = MutableLiveData<SurveyDataSource>()

    override fun create(): DataSource<Int, Survey> {
        val dataSource = SurveyDataSource(surveyRepository)
        dataSourceLive.postValue(dataSource)
        return dataSource
    }
}