package co.nimblehq.di.module

import co.nimblehq.account.Account
import co.nimblehq.authenticator.TokenAuthenticator
import co.nimblehq.data.source.token.TokenRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Created by Viktor Artemiev on 2019-08-02.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Module
class NetworkModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(30000, TimeUnit.MILLISECONDS)
            .connectTimeout(30000, TimeUnit.MILLISECONDS)
            .pingInterval(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Singleton
    @Provides
    fun provideTokenAuthenticator(account: Account, tokenRepository: TokenRepository) =
        TokenAuthenticator(account, tokenRepository)
}