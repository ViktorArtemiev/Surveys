package co.nimblehq.data.survey

import co.nimblehq.data.BaseServiceTest
import co.nimblehq.data.model.Survey
import co.nimblehq.data.source.survey.SurveyService
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.Is
import org.hamcrest.core.IsNull
import org.junit.Assert.*
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
class SurveyServiceTest : BaseServiceTest() {

    private lateinit var service: SurveyService

    @Before
    override fun setup() {
        super.setup()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(SurveyService::class.java)
    }

    @Test
    fun `get surveys`() {
        mockResponse("surveys.json")
        runBlocking {
            val response: Response<List<Survey>> = service.getSurveys(page = 1, perPage = 10).await()
            val surveys = response.body()
            assertThat(surveys, IsNull.notNullValue())
            assertFalse(surveys!!.isEmpty())
            assertTrue(surveys.size == 10)
            val survey = surveys.first()
            assertThat(survey.id, Is.`is`("df5c6cad88e3865aede0"))
            assertThat(survey.title, Is.`is`("Oishi Buffet"))
            assertThat(survey.description, Is.`is`("Customer Experience Survey"))
            assertThat(survey.imageUrl, Is.`is`("https://dhdbhh0jsld0o.cloudfront.net/m/6b663dd6fb7a200ad4c7_"))
        }
    }

}