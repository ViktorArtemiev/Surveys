package co.nimblehq.data.source.token

import co.nimblehq.data.model.Token
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
interface TokenService {

    @POST("/oauth/token")
    fun refreshToken(
        @Query("grant_type") grantType: String = "password",
        @Query("username") username: String,
        @Query("password") password: String
    ): Deferred<Response<Token>>

}