package co.nimblehq.di.module

import co.nimblehq.BuildConfig
import co.nimblehq.SurveysAccount
import co.nimblehq.data.source.surveys.SurveysRepository
import co.nimblehq.data.source.surveys.SurveysService
import co.nimblehq.data.source.token.TokenService
import co.nimblehq.data.source.token.TokenSource
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Module
class DataModule {

    @Singleton
    @Provides
    fun provideSurveyRepository(service: SurveysService) = SurveysRepository(service)

    @Singleton
    @Provides
    fun provideSurveysService(interceptor: AuthorizationInterceptor) = buildRetrofit(
        createOkHttpClientBuilder()
            .addInterceptor(interceptor)
            .build()
    ).create(SurveysService::class.java)

    @Singleton
    @Provides
    fun provideAuthorizationInterceptor(account: SurveysAccount, tokenSource: TokenSource) =
        AuthorizationInterceptor(account, tokenSource)

    @Singleton
    @Provides
    fun provideTokenRepository(service: TokenService) = TokenSource(service)

    @Singleton
    @Provides
    fun provideTokenService() = buildRetrofit(
        createOkHttpClientBuilder()
            .build()
    ).create(TokenService::class.java)

    fun buildRetrofit(httpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.ENDPOINT)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    fun createOkHttpClientBuilder() = OkHttpClient.Builder()
        .readTimeout(30000, TimeUnit.MILLISECONDS)
        .connectTimeout(30000, TimeUnit.MILLISECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

}

class AuthorizationInterceptor(
    private val account: SurveysAccount,
    private val tokenSource: TokenSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = setAuthorizationHeader(chain.request())
        var response = chain.proceed(request)
        if (response.code() == 401) { //if unauthorized
            runBlocking {
                val token = tokenSource.refreshToken(account.provideUser())
                account.refreshToken(token)
                response = chain.proceed(setAuthorizationHeader(request))
            }
        }
        return response
    }

    fun setAuthorizationHeader(request: Request): Request {
        return request.newBuilder()
            .header("Authorization", account.provideToken())
            .build()
    }
}