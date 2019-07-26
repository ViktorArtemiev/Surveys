package co.nimblehq.data.source.surveys


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveysSource(val service: SurveysService) {

    suspend fun getSurveys(): String {
       val response =  service.getSurveys(1, 10).await()
        return if (response.isSuccessful) {
            response.body()!!
        } else {
            ""
        }
    }

}