package co.nimblehq.data.source.surveys

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import co.nimblehq.data.model.Survey


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveysDataSourceFactory(private val surveysRepository: SurveysRepository) : DataSource.Factory<Int, Survey>() {

    val dataSourceLive = MutableLiveData<SurveyDataSource>()

    override fun create(): DataSource<Int, Survey> {
        val dataSource = SurveyDataSource(surveysRepository)
        dataSourceLive.postValue(dataSource)
        return dataSource
    }
}