package com.archer.github.app.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.model.TrendRepoResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockRepo: GithubRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getTrendRepoList success should update repoList and loadingState`() = runTest {
        // Arrange
        val mockList = listOf<TrendRepoResponse>(mockk())
        coEvery { mockRepo.getTrendRepoList(any(), any(), any(), any()) } returns Result.success(mockList)

        val viewModel = HomeViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act
        viewModel.getTrendRepoList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(mockList, viewModel.repoList.value)
        assertFalse(viewModel.loadingState.value)
        coVerify(exactly = 1) { mockRepo.getTrendRepoList(any(), any(), any(), any()) }
    }

    @Test
    fun `getTrendRepoList failure should emit error and stop loading`() = runTest {
        // Arrange
        val exception = Exception("Network failed")
        coEvery { mockRepo.getTrendRepoList(any(), any(), any(), any()) } returns Result.failure(exception)

        val viewModel = HomeViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act
        viewModel.getTrendRepoList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.repoList.value == null)
        assertFalse(viewModel.loadingState.value)

        coVerify(exactly = 1) { mockRepo.getTrendRepoList(any(), any(), any(), any()) }
    }

    @Test
    fun `getTrendRepoList should skip fetch if not refresh and repoList is not null`() = runTest {
        // Arrange
        val mockList = listOf<TrendRepoResponse>(mockk())
        coEvery { mockRepo.getTrendRepoList(any(), any(), any(), any()) } returns Result.success(mockList)

        val viewModel = HomeViewModel().apply {
            repoProvider = { mockRepo }
        }
        viewModel.getTrendRepoList() // first fetch
        testDispatcher.scheduler.advanceUntilIdle()

        // Act
        viewModel.getTrendRepoList(isRefresh = false) // should skip
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) { mockRepo.getTrendRepoList(any(), any(), any(), any()) } // only 1 call
    }
}
