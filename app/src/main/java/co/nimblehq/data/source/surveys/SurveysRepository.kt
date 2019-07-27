package co.nimblehq.data.source.surveys

import co.nimblehq.data.model.Survey
import timber.log.Timber


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveysRepository(private val service: SurveysService) {

    suspend fun getSurveys(page: Int, pageSize: Int): List<Survey> {
        return try {
            val response = service.getSurveys(page, pageSize).await()
            if (response.isSuccessful) {
                response.body()!!
            } else {
                emptyList()
            }
        } catch (ex: Throwable) {
            Timber.e(ex)
            emptyList()
        }
    }

}