package co.nimblehq.data

import androidx.annotation.CallSuper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.After
import org.junit.Before
import org.junit.Rule


/**
 * Created by Viktor Artemiev on 2019-07-28.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
abstract class BaseServiceTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    protected lateinit var mockWebServer: MockWebServer

    @Before
    @CallSuper
    open fun setup() {
        mockWebServer = MockWebServer()
    }

    protected fun mockResponse(fileName: String) {
        val inputStream = getJsonInputStream(fileName)
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        mockWebServer.enqueue(
            mockResponse
                .setBody(source.readString(Charsets.UTF_8))
        )
    }

    private fun getJsonInputStream(fileName: String) = javaClass.classLoader!!
        .getResourceAsStream("json/$fileName")

    @After
    fun shutdown() {
        mockWebServer.shutdown()
    }

}