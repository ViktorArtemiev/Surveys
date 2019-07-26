package co.nimblehq.data.source.surveys

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
interface SurveysService {

    @GET("/surveys.json")
    fun getSurveys(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Deferred<Response<String>>

}