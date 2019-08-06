package co.nimblehq.screen.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import co.nimblehq.data.model.Survey
import co.nimblehq.data.source.survey.SurveyDataSourceFactory
import co.nimblehq.data.source.survey.SurveyRepository
import co.nimblehq.data.source.survey.SurveyService
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import retrofit2.Response
import java.io.IOException


/**
 * Created by Viktor Artemiev on 2019-08-05.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    lateinit var service: SurveyService
    lateinit var viewModel: MainViewModel

    @Before
    @ExperimentalCoroutinesApi
    fun setup() {
        service = mock(SurveyService::class.java)
        val repository = SurveyRepository(service)
        val sourceFactory = SurveyDataSourceFactory(repository, Dispatchers.Unconfined)
        viewModel = MainViewModel(sourceFactory)
    }

    @Test
    fun `initial load`() {
        val itemCountObserver = mock(Observer::class.java) as Observer<Int>
        viewModel.itemCountLive.observeForever(itemCountObserver)

        val initialLoadObserver = mock(Observer::class.java) as Observer<Boolean>
        viewModel.initialLoadLive.observeForever(initialLoadObserver)

        val surveys = (1..10).map { mockSurvey(it.toString()) }
        `when`(service.getSurveys(1, 2 * MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(Response.success(surveys)))
        val pagedList = getPagedList(viewModel.surveysLive)
        pagedList.loadAllData()
        assertThat(pagedList, `is`(surveys))

        verify(itemCountObserver).onChanged(10)
        verify(initialLoadObserver, times(2)).onChanged(any())
        val inOrder = inOrder(initialLoadObserver)
        inOrder.verify(initialLoadObserver).onChanged(true)
        inOrder.verify(initialLoadObserver).onChanged(false)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun `initial load fails`() {
        val itemCountObserver = mock(Observer::class.java) as Observer<Int>
        viewModel.itemCountLive.observeForever(itemCountObserver)

        val initialLoadObserver = mock(Observer::class.java) as Observer<Boolean>
        viewModel.initialLoadLive.observeForever(initialLoadObserver)

        val errorObserver = mock(Observer::class.java) as Observer<Throwable>
        viewModel.errorLive.observeForever(errorObserver)

        `when`(service.getSurveys(1, 2 * MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(
                Response.error(401, ResponseBody.create(
                    MediaType.parse("application/json"), byteArrayOf()))))

        val pagedList = getPagedList(viewModel.surveysLive)
        pagedList.loadAllData()
        assertTrue(pagedList.isEmpty())

        verify(itemCountObserver, never()).onChanged(any())
        verify(initialLoadObserver, times(1)).onChanged(any())
        val inOrder = inOrder(initialLoadObserver)
        inOrder.verify(initialLoadObserver).onChanged(true)
        inOrder.verifyNoMoreInteractions()
        verify(errorObserver).onChanged(any(IOException::class.java))
    }

    @Test
    fun `retry on initial load fails`() {
        val itemCountObserver = mock(Observer::class.java) as Observer<Int>
        viewModel.itemCountLive.observeForever(itemCountObserver)

        val initialLoadObserver = mock(Observer::class.java) as Observer<Boolean>
        viewModel.initialLoadLive.observeForever(initialLoadObserver)

        val errorObserver = mock(Observer::class.java) as Observer<Throwable>
        viewModel.errorLive.observeForever(errorObserver)

        `when`(service.getSurveys(1, 2 * MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(
                Response.error(401, ResponseBody.create(
                    MediaType.parse("application/json"), byteArrayOf()))))

        val pagedList = getPagedList(viewModel.surveysLive)
        pagedList.loadAllData()
        assertTrue(pagedList.isEmpty())

        verify(itemCountObserver, never()).onChanged(any())
        verify(initialLoadObserver, times(1)).onChanged(any())
        verify(errorObserver).onChanged(any(IOException::class.java))

        val surveys = (1..10).map { mockSurvey(it.toString()) }
        `when`(service.getSurveys(1, 2 * MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(Response.success(surveys)))
        viewModel.retry()
        pagedList.loadAllData()
        assertThat(pagedList, `is`(surveys))

        verify(itemCountObserver).onChanged(10)
        verify(initialLoadObserver, times(3)).onChanged(any())
        val inOrder = inOrder(initialLoadObserver)
        inOrder.verify(initialLoadObserver, times(2)).onChanged(true)
        inOrder.verify(initialLoadObserver).onChanged(false)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun `refresh load`() {
        val itemCountObserver = mock(Observer::class.java) as Observer<Int>
        viewModel.itemCountLive.observeForever(itemCountObserver)

        val initialLoadObserver = mock(Observer::class.java) as Observer<Boolean>
        viewModel.initialLoadLive.observeForever(initialLoadObserver)

        val surveys = (1..10).map { mockSurvey(it.toString()) }
        `when`(service.getSurveys(1, 2 * MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(Response.success(surveys)))

        val pagedList = getPagedList(viewModel.surveysLive)
        pagedList.loadAllData()
        assertThat(pagedList, `is`(surveys))

        viewModel.refresh()
        pagedList.loadAllData()
        assertThat(pagedList, `is`(surveys))

        verify(itemCountObserver, times(2)).onChanged(10)
        verify(initialLoadObserver, times(4)).onChanged(any())
        val inOrder = inOrder(initialLoadObserver)
        inOrder.verify(initialLoadObserver, times(2)).onChanged(true)
        inOrder.verify(initialLoadObserver).onChanged(false)
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun `after load`() {
        val itemCountObserver = mock(Observer::class.java) as Observer<Int>
        viewModel.itemCountLive.observeForever(itemCountObserver)

        val initialLoadObserver = mock(Observer::class.java) as Observer<Boolean>
        viewModel.initialLoadLive.observeForever(initialLoadObserver)

        val errorObserver = mock(Observer::class.java) as Observer<Throwable>
        viewModel.errorLive.observeForever(errorObserver)

        val initialSurveys = (1..10).map { mockSurvey(it.toString()) }
        `when`(service.getSurveys(1, 2 * MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(Response.success(initialSurveys)))

        val afterSurveys = (10..15).map { mockSurvey(it.toString()) }
        `when`(service.getSurveys(2, MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(Response.success(afterSurveys)))

        val totalSurveys = mutableListOf<Survey>().apply {
            addAll(initialSurveys)
            addAll(afterSurveys)
        }

        val pagedList = getPagedList(viewModel.surveysLive)
        pagedList.loadAllData()
        assertThat(pagedList, `is`(totalSurveys))

        verify(initialLoadObserver, times(2)).onChanged(any())
        val inInitialOrder = inOrder(initialLoadObserver)
        inInitialOrder.verify(initialLoadObserver).onChanged(true)
        inInitialOrder.verify(initialLoadObserver).onChanged(false)
        inInitialOrder.verifyNoMoreInteractions()
    }

    @Test
    fun `retry on after load fails`() {
        val itemCountObserver = mock(Observer::class.java) as Observer<Int>
        viewModel.itemCountLive.observeForever(itemCountObserver)

        val initialLoadObserver = mock(Observer::class.java) as Observer<Boolean>
        viewModel.initialLoadLive.observeForever(initialLoadObserver)

        val errorObserver = mock(Observer::class.java) as Observer<Throwable>
        viewModel.errorLive.observeForever(errorObserver)

        val initialSurveys = (1..10).map { mockSurvey(it.toString()) }
        `when`(service.getSurveys(1,2 * MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(Response.success(initialSurveys)))

        `when`(service.getSurveys(2, MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(
                Response.error(401, ResponseBody.create(
                    MediaType.parse("application/json"), byteArrayOf()))))

        val pagedList = getPagedList(viewModel.surveysLive)
        pagedList.loadAllData()
        verify(errorObserver).onChanged(any(IOException::class.java))

        verify(initialLoadObserver, times(2)).onChanged(any())
        val inInitialOrder = inOrder(initialLoadObserver)
        inInitialOrder.verify(initialLoadObserver).onChanged(true)
        inInitialOrder.verify(initialLoadObserver).onChanged(false)
        inInitialOrder.verifyNoMoreInteractions()

        val afterSurveys = (10..14).map { mockSurvey(it.toString()) }
        `when`(service.getSurveys(2, MainViewModel.PAGE_SIZE))
            .thenReturn(CompletableDeferred(Response.success(afterSurveys)))
        viewModel.retry()
        pagedList.loadAllData()

        val totalSurveys = mutableListOf<Survey>().apply {
            addAll(initialSurveys)
            addAll(afterSurveys)
        }
        assertThat(pagedList, `is`(totalSurveys))
        verify(itemCountObserver).onChanged(15)
    }

    private fun mockSurvey(id: String): Survey {
        return Survey(
            id = id,
            title = "Super Store $id",
            description = "Customer Experience Survey $id",
            imageUrl = "https://dhdbhh0jsld0o.cloudfront.net/m/$id"
        )
    }

    /**
     * Extract the latest paged list
     */
    private fun getPagedList(surveysLive: LiveData<PagedList<Survey>>): PagedList<Survey> {
        val observer = LoggingObserver<PagedList<Survey>>()
        surveysLive.observeForever(observer)
        assertThat(observer.value, `is`(notNullValue()))
        return observer.value!!
    }

    /**
     * Simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value: T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }

    private fun <T> PagedList<T>.loadAllData() {
        if (this.loadedCount == 0) return
        do {
            val oldSize = this.loadedCount
            this.loadAround(this.size - 1)
        } while (this.size != oldSize)
    }

}
