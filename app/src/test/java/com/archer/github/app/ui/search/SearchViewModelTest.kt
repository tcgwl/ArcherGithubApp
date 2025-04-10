package com.archer.github.app.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.model.Repository
import com.archer.github.app.logic.model.SearchResultResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
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
        unmockkAll()
    }

    @Test
    fun `refresh should load first page successfully`() = runTest {
        // Arrange
        val testQuery = "query"
        val mockItems = listOf(mockk<Repository>())
        val mockResponse = mockk<SearchResultResponse<Repository>>().apply {
            every { items } returns mockItems
        }
        coEvery { mockRepo.searchRepositories(testQuery, 1) } returns Result.success(mockResponse)

        val viewModel = SearchViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act - Trigger refresh (searchRepositories with first page)
        viewModel.refresh(testQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert - Check if data is loaded and page is reset to 1
        assertEquals(mockItems, viewModel.searchRepoResult.value)
        assertEquals(1, viewModel.currentPage)
        coVerify(exactly = 1) { mockRepo.searchRepositories(testQuery, 1) }
    }

    @Test
    fun `loadMore should load next page successfully when not at last page`() = runTest {
        // Arrange
        val testQuery = "query"
        val mockItems1 = List(30) { mockk<Repository>() } // Simulating first page results
        val mockItems2 = List(30) { mockk<Repository>() } // Simulating second page results
        val mockResponse1 = mockk<SearchResultResponse<Repository>>().apply {
            every { items } returns mockItems1
        }
        val mockResponse2 = mockk<SearchResultResponse<Repository>>().apply {
            every { items } returns mockItems2
        }

        coEvery { mockRepo.searchRepositories(testQuery, 1) } returns Result.success(mockResponse1)
        coEvery { mockRepo.searchRepositories(testQuery, 2) } returns Result.success(mockResponse2)

        val viewModel = SearchViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act - Trigger initial search (first page)
        viewModel.refresh(testQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act - Trigger load more (second page)
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert - Check if data for second page is loaded and page incremented
        assertEquals(mockItems1 + mockItems2, viewModel.searchRepoResult.value)
        assertEquals(2, viewModel.currentPage)
        coVerify(exactly = 1) { mockRepo.searchRepositories(testQuery, 1) }
        coVerify(exactly = 1) { mockRepo.searchRepositories(testQuery, 2) }
    }

    @Test
    fun `loadMore should not load more if already at last page`() = runTest {
        // Arrange
        val testQuery = "query"
        val mockItems = List(30) { mockk<Repository>() }
        val mockResponse = mockk<SearchResultResponse<Repository>>().apply {
            every { items } returns mockItems
        }

        coEvery { mockRepo.searchRepositories(testQuery, 1) } returns Result.success(mockResponse)

        val viewModel = SearchViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act - Trigger initial search (first page)
        viewModel.refresh(testQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Simulate last page
        viewModel.isLastPage = true

        // Act - Trigger load more (should not load anything since it's last page)
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert - Check that no new data is loaded and page stays at 1
        assertEquals(mockItems, viewModel.searchRepoResult.value)
        assertEquals(1, viewModel.currentPage)
        coVerify(exactly = 1) { mockRepo.searchRepositories(testQuery, 1) }
        coVerify(exactly = 0) { mockRepo.searchRepositories(testQuery, 2) }
    }

    @Test
    fun `loadMore should handle error and rollback page`() = runTest {
        // Arrange
        val testQuery = "query"
        val mockItems = List(30) { mockk<Repository>() }
        val mockResponse = mockk<SearchResultResponse<Repository>>().apply {
            every { items } returns mockItems
        }

        coEvery { mockRepo.searchRepositories(testQuery, 1) } returns Result.success(mockResponse)
        coEvery { mockRepo.searchRepositories(testQuery, 2) } returns Result.failure(Exception("Network error"))

        val viewModel = SearchViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act - Trigger initial search (first page)
        viewModel.refresh(testQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Act - Trigger load more (second page, which will fail)
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert - Check that page is rolled back and no new data is loaded
        assertEquals(mockItems, viewModel.searchRepoResult.value)
        assertEquals(1, viewModel.currentPage)  // Page should roll back
        coVerify(exactly = 1) { mockRepo.searchRepositories(testQuery, 1) }
        coVerify(exactly = 1) { mockRepo.searchRepositories(testQuery, 2) }
    }
}
