package com.archer.github.app.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.model.Repository
import com.archer.github.app.logic.model.SearchResultResponse
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
class SearchViewModelTest {

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
    fun `getUserRepoList success should update userRepoList and loadingState`() = runTest {
        // Arrange
        val testQuery = "query"
        val mockResponse = mockk<SearchResultResponse<Repository>>()
        coEvery { mockRepo.searchRepositories(testQuery) } returns Result.success(mockResponse)

        val viewModel = SearchViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act
        viewModel.searchRepositories(testQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(mockResponse, viewModel.searchRepoResult.value)
        assertFalse(viewModel.loadingState.value)
        coVerify(exactly = 1) { mockRepo.searchRepositories(testQuery) }
    }

    @Test
    fun `getUserRepoList failure should emit error and stop loading`() = runTest {
        // Arrange
        val testQuery = "query"
        coEvery { mockRepo.searchRepositories(testQuery) } returns Result.failure(Exception("Network error"))

        val viewModel = SearchViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act
        viewModel.searchRepositories(testQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.searchRepoResult.value == null)
        assertFalse(viewModel.loadingState.value)

        coVerify(exactly = 1) { mockRepo.searchRepositories(testQuery) }
    }
}
