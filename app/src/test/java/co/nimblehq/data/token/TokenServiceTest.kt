package co.nimblehq.data.token

import co.nimblehq.data.BaseServiceTest
import co.nimblehq.data.model.Token
import co.nimblehq.data.source.token.TokenService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.Is
import org.hamcrest.core.IsNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Viktor Artemiev on 2019-07-28.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@RunWith(JUnit4::class)
class TokenServiceTest : BaseServiceTest() {

    private lateinit var service: TokenService

    @Before
    override fun setup() {
        super.setup()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(TokenService::class.java)
    }

    @Test
    fun `refresh token`() {
        mockResponse("token.json")
        runBlocking {
            val response: Response<Token> = service.refreshToken(username = "test@nimblehq.co", password = "123456qwe").await()
            val token = response.body()
            assertThat(token, IsNull.notNullValue())
            assertThat(token!!.accessToken, Is.`is`("32e059898c1a015e55c0f492acc5074839ac5305bd779cd731592562d92c1267"))
            assertThat(token.type, Is.`is`("bearer"))
            assertThat(token.expiresIn, Is.`is`(7200))
        }
    }
}