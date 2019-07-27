package co.nimblehq.data.source.surveys

import co.nimblehq.data.model.Survey
import java.io.IOException


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveysRepository(private val service: SurveysService) {

    suspend fun getSurveys(page: Int, pageSize: Int): List<Survey> {
        val response = service.getSurveys(page, pageSize).await()
        return response.body() ?: throw IOException(response.message())
    }

}