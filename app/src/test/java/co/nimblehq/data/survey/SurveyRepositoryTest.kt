package co.nimblehq.data.survey

import co.nimblehq.data.model.Survey
import co.nimblehq.data.source.survey.SurveyRepository
import co.nimblehq.data.source.survey.SurveyService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.*
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
class SurveyRepositoryTest {

    lateinit var service: SurveyService
    lateinit var surveys: List<Survey>

    @Before
    fun setup() {
        service = mock(SurveyService::class.java)
        surveys = mutableListOf(
            Survey(
                id = "1",
                title = "Oishi Buffet",
                description = "Customer Experience Survey",
                imageUrl = "https://dhdbhh0jsld0o.cloudfront.net/m/1"
            ),
            Survey(
                id = "2",
                title = "Segafredo",
                description = "Customer Satisfaction Survey",
                imageUrl = "https://dhdbhh0jsld0o.cloudfront.net/m/2"
            ),
            Survey(
                id = "3",
                title = "Tops Super Store",
                description = "Customer Survey",
                imageUrl = "https://dhdbhh0jsld0o.cloudfront.net/m/3"
            ))
    }

    @Test
    fun `when get surveys - calls the correct API method`() {
        val repository = SurveyRepository(service)
        `when`(service.getSurveys(page = 1, perPage = 3))
            .thenReturn(CompletableDeferred(Response.success(surveys)))
        runBlocking {
            repository.getSurveys(page = 1, pageSize = 3)
            verify(service).getSurveys(page = 1, perPage = 3)
        }
    }

    @Test
    fun `get surveys and succeed`() {
        val repository = SurveyRepository(service)
        `when`(service.getSurveys(page = 1, perPage = 3))
            .thenReturn(CompletableDeferred(Response.success(surveys)))
        runBlocking {
            val surveys = repository.getSurveys(page = 1, pageSize = 3)
            assertNotNull(surveys)
            assertFalse(surveys.isEmpty())
            assertEquals(3, surveys.size)
            assertEquals("1", surveys.first().id)
            assertEquals("Oishi Buffet", surveys.first().title)
            assertEquals("Customer Experience Survey", surveys.first().description)
            assertEquals("https://dhdbhh0jsld0o.cloudfront.net/m/1", surveys.first().imageUrl)
        }
    }

    @Test(expected = IOException::class)
    fun `get surveys and fail`() {
        val repository = SurveyRepository(service)
        `when`(service.getSurveys(page = 1, perPage = 3))
            .thenReturn(
                CompletableDeferred(
                    Response.error(401, ResponseBody.create(
                        MediaType.parse("application/json"), byteArrayOf())))
            )
        runBlocking {
            repository.getSurveys(page = 1, pageSize = 3)
        }
    }
}