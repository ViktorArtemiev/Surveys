package co.nimblehq.data.source.survey

import co.nimblehq.data.model.Survey
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

/**
 * Survey API access points
 */
interface SurveyService {

    /** Provides a response [List] of [Survey] data objects. */
    @GET("/surveys.json")
    fun getSurveys(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Deferred<Response<List<Survey>>>

}