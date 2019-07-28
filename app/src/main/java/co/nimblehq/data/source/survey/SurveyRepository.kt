package co.nimblehq.data.source.survey

import co.nimblehq.data.model.Survey
import java.io.IOException


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

/**
 * The Repository provides an interface to fetch [Survey] data.
 *
 * It provides a clean API so that the rest of the app can retrieve [Survey] data easily.
 * It knows where to get the data from and what API calls to make.
 *
 * @param service {@link Retrofit} interface that loads [Survey] data from network.
 */
class SurveyRepository(private val service: SurveyService) {

    /**
     * Fetches [List] of [Survey] from network.
     *
     * @param page number used to query [Survey] data pages
     * @param pageSize number of [Survey] items to load
     * @return loaded [List] of [Survey]
     * @throws Throwable any error during the process
     */
    suspend fun getSurveys(page: Int, pageSize: Int): List<Survey> {
        val response = service.getSurveys(page, pageSize).await()
        return response.body() ?: throw IOException(response.message())
    }

}