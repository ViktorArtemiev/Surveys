package co.nimblehq.authenticator

import co.nimblehq.account.Account
import co.nimblehq.data.source.token.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


/**
 * Created by Viktor Artemiev on 2019-08-01.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class TokenAuthenticator(
    private val account: Account,
    private val tokenRepository: TokenRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return if (response.code() == 401) { //if unauthorized
            refreshToken()
            response.request()
                .newBuilder()
                .header("Authorization", account.provideToken())
                .build()
        } else {
            null
        }
    }

    private fun refreshToken() {
        runBlocking {
            val token = tokenRepository.refreshToken(account.provideUser())
            account.refreshToken(token)
        }
    }
}