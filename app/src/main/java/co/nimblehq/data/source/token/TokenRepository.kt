package co.nimblehq.data.source.token

import co.nimblehq.data.model.Token
import co.nimblehq.data.model.User
import java.io.IOException


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

/**
 * The Repository provides an interface to fetch [Token] data.
 *
 * It provides a clean API so that the rest of the app can retrieve [Token] data easily.
 * It knows where to get the data from and what API calls to make.
 *
 * @param service {@link Retrofit} interface that loads [Token] data from network.
 */
class TokenRepository(private val service: TokenService) {

    /**
     * Fetches [Token] from network.
     *
     * @param user
     * @return [Token]
     * @throws Throwable any error during the process
     */
    suspend fun refreshToken(user: User): Token {
        val response = service.refreshToken(username = user.username, password = user.password).await()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw IOException(response.message())
        }
    }
}