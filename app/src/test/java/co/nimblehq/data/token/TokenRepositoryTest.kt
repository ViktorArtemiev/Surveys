package co.nimblehq.data.token

import co.nimblehq.data.model.Token
import co.nimblehq.data.model.User
import co.nimblehq.data.source.token.TokenRepository
import co.nimblehq.data.source.token.TokenService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import retrofit2.Response
import java.io.IOException


/**
 * Created by Viktor Artemiev on 2019-07-28.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@RunWith(JUnit4::class)
class TokenRepositoryTest {

    lateinit var service: TokenService
    lateinit var user: User
    lateinit var token: Token

    @Before
    fun setup() {
        service = mock(TokenService::class.java)
        user = User(username = "test@nimblehq.co", password = "123456qwe")
        token = Token(
            accessToken = "32e059898c1a015e55c0f492acc5074839ac5305bd779cd731592562d92c1267",
            type = "bearer",
            expiresIn = 7200
        )
    }

    @Test
    fun `when refresh token - calls the correct API method`() {
        val repository = TokenRepository(service)
        `when`(service.refreshToken(username = user.username, password = user.password))
            .thenReturn(CompletableDeferred(Response.success(token)))
        runBlocking {
            repository.refreshToken(user)
            verify(service).refreshToken(username = user.username, password = user.password)
        }
    }

    @Test
    fun `refresh token and succeed`() {
        val repository = TokenRepository(service)
        `when`(service.refreshToken(username = user.username, password = user.password))
            .thenReturn(CompletableDeferred(Response.success(token)))
        runBlocking {
            val token = repository.refreshToken(user)
            assertNotNull(token)
            assertEquals(token.accessToken, "32e059898c1a015e55c0f492acc5074839ac5305bd779cd731592562d92c1267")
            assertEquals(token.type, "bearer")
            assertEquals(token.expiresIn, 7200)
        }
    }

    @Test(expected = IOException::class)
    fun `refresh token and fail`() {
        val repository = TokenRepository(service)
        `when`(service.refreshToken(username = user.username, password = user.password))
            .thenReturn(CompletableDeferred(Response.error(401, ResponseBody.create(
                MediaType.parse("application/json"),
                "{\n" +
                        "\"error\": \"invalid_grant\",\n" +
                        "}"
            ))))
        runBlocking {
          repository.refreshToken(user)
        }
    }

}