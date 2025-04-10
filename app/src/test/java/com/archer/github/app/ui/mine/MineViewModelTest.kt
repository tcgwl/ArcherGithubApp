package com.archer.github.app.ui.mine

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.local.UserDao
import com.archer.github.app.logic.model.Repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class MineViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockRepo: GithubRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        mockkStatic(UserDao::class)
        val mockContext = mockk<Context>(relaxed = true)
        UserDao.contextProvider = { mockContext }

        val mockPrefs = mockk<SharedPreferences>(relaxed = true)
        val mockEditor = mockk<SharedPreferences.Editor>(relaxed = true)
        every { mockPrefs.edit() } returns mockEditor
        every { mockEditor.putString(any(), any()) } returns mockEditor
        every { mockEditor.remove(any()) } returns mockEditor
        every { mockEditor.apply() } just Runs
        every { mockPrefs.getString("access_token", null) } returns "mocked_token"

        UserDao.preferencesProvider = { mockPrefs }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
        UserDao.preferencesProvider = null
    }

    @Test
    fun `getUserRepoList success should update userRepoList and loadingState`() = runTest {
        // Arrange
        val testToken = "valid_token"
        val mockList = listOf<Repository>(mockk())
        every { UserDao.getAccessToken() } returns testToken
        coEvery { mockRepo.getUserRepoList(testToken) } returns Result.success(mockList)

        val viewModel = MineViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act
        viewModel.getUserRepoList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(mockList, viewModel.userRepoList.value)
        assertFalse(viewModel.loadingState.value)
        coVerify(exactly = 1) { mockRepo.getUserRepoList(testToken) }
    }

    @Test
    fun `getUserRepoList failure should emit error and stop loading`() = runTest {
        // Arrange
        val invalidToken = "invalid_token"
        every { UserDao.getAccessToken() } returns invalidToken
        coEvery { mockRepo.getUserRepoList(invalidToken) } returns Result.failure(Exception("Invalid token"))

        val viewModel = MineViewModel().apply {
            repoProvider = { mockRepo }
        }

        // Act
        viewModel.getUserRepoList()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.userRepoList.value == null)
        assertFalse(viewModel.loadingState.value)

        coVerify(exactly = 1) { mockRepo.getUserRepoList(invalidToken) }
    }
}
