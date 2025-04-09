package com.archer.github.app.ui.mine

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.local.UserDao
import com.archer.github.app.logic.model.AccessTokenResponse
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OAuthLoginViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(UserDao::class)
        val mockContext = mockk<Context>(relaxed = true)
        UserDao.contextProvider = { mockContext }

        val mockPrefs = mockk<SharedPreferences>(relaxed = true).apply {
            every { edit() } returns mockk(relaxed = true)
        }
        every { mockContext.getSharedPreferences(any(), any()) } returns mockPrefs
        every { UserDao.saveAccessToken(any()) } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `requestToken success should save token and call onFinish with false`() = runTest {
        // Arrange
        val mockRepo = mockk<GithubRepository>()
        val testToken = "valid_token"
        val tokenResponse = mockk<AccessTokenResponse>() {
            every { access_token } returns testToken
        }

        coEvery {
            mockRepo.oauthLogin(any(), any(), any())
        } returns Result.success(tokenResponse)

        val viewModel = OAuthLoginViewModel().apply {
            repoProvider = { mockRepo }
        }
        var onFinishResult: Boolean? = null

        // Act
        viewModel.requestToken("code123") { onFinishResult = it }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { mockRepo.oauthLogin(any(), any(), "code123") }
        verify { UserDao.saveAccessToken(testToken) }
        assertEquals(false, onFinishResult)
    }

    @Test
    fun `requestToken failure should not save token and call onFinish with true`() = runTest {
        // Arrange
        val mockRepo = mockk<GithubRepository>()

        coEvery {
            mockRepo.oauthLogin(any(), any(), any())
        } returns Result.failure(Exception("Network error"))

        val viewModel = OAuthLoginViewModel().apply {
            repoProvider = { mockRepo }
        }
        var onFinishResult: Boolean? = null

        // Act
        viewModel.requestToken("code456") { onFinishResult = it }
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        coVerify { mockRepo.oauthLogin(any(), any(), "code456") }
        verify(exactly = 0) { UserDao.saveAccessToken(any()) }
        assertEquals(true, onFinishResult)
    }
}
