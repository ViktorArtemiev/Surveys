package co.nimblehq.di.module

import co.nimblehq.BuildConfig
import co.nimblehq.authenticator.TokenAuthenticator
import co.nimblehq.data.source.survey.SurveyRepository
import co.nimblehq.data.source.survey.SurveyService
import co.nimblehq.data.source.token.TokenRepository
import co.nimblehq.data.source.token.TokenService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideSurveyRepository(service: SurveyService) = SurveyRepository(service)

    @Singleton
    @Provides
    fun provideSurveysService(httpClient: OkHttpClient, authenticator: TokenAuthenticator): SurveyService {
        return buildRetrofit(
            httpClient
                .newBuilder()
                .authenticator(authenticator)
                .build()
        ).create(SurveyService::class.java)
    }

    @Singleton
    @Provides
    fun provideTokenRepository(service: TokenService) = TokenRepository(service)

    @Singleton
    @Provides
    fun provideTokenService(httpClient: OkHttpClient): TokenService {
        return buildRetrofit(httpClient)
            .create(TokenService::class.java)
    }

    private fun buildRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }
}