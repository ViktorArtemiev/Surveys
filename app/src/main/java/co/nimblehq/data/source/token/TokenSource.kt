package co.nimblehq.data.source.token

import co.nimblehq.data.model.Token
import co.nimblehq.data.model.User
import java.io.IOException


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class TokenSource(private val service: TokenService) {

    suspend fun refreshToken(user: User): Token {
        val response = service.refreshToken(username = user.username, password = user.password).await()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw IOException(response.message())
        }
    }
}