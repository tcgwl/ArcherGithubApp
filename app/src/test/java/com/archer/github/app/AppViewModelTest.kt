package com.archer.github.app

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.archer.github.app.logic.GithubRepository
import com.archer.github.app.logic.local.UserDao
import com.archer.github.app.logic.model.LoginState
import com.archer.github.app.logic.model.User
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AppViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var mockRepo: GithubRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)

        // 解耦关键：替换 UserDao 的 Context 来源
        mockkStatic(UserDao::class)
        val mockContext = mockk<Context>(relaxed = true)
        UserDao.contextProvider = { mockContext } // 注入测试上下文

        // 模拟 SharedPreferences
        val mockPrefs = mockk<SharedPreferences>(relaxed = true).apply {
            every { edit() } returns mockk(relaxed = true)
        }
        every { mockContext.getSharedPreferences(any(), any()) } returns mockPrefs

        // 默认模拟 UserDao 方法
        every { UserDao.getAccessToken() } returns null
        every { UserDao.removeAccessToken() } just Runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `checkLoginState with valid token should update login state`() = runTest {
        // Arrange
        val testToken = "valid_token"
        val mockUser = mockk<User>().apply {
            every { id } returns "1"
            every { login } returns "testUser"
        }

        // 1：确保模拟对象被注入
        every { UserDao.getAccessToken() } returns testToken
        coEvery { mockRepo.getUserInfo(testToken) } returns Result.success(mockUser)

        // 2：显式注入依赖
        val viewModel = AppViewModel(mockRepo).apply {
            checkLoginState()
            testDispatcher.scheduler.advanceUntilIdle()
        }

        assertTrue(viewModel.loginState.value.isLoggedIn)
        assertEquals(mockUser, viewModel.loginState.value.user)
        coVerify(exactly = 1) { mockRepo.getUserInfo(testToken) }
    }

    @Test
    fun `checkLoginState with invalid token should logout`() = runTest {
        // Arrange
        val invalidToken = "invalid_token"
        every { UserDao.getAccessToken() } returns invalidToken
        coEvery { mockRepo.getUserInfo(invalidToken) } returns Result.failure(Exception("Invalid token"))

        // Act
        val viewModel = AppViewModel(mockRepo).apply {
            checkLoginState()
            testDispatcher.scheduler.advanceUntilIdle()
        }

        // Assert
        assertFalse(viewModel.loginState.value.isLoggedIn)
        coVerify(exactly = 1) { mockRepo.getUserInfo(invalidToken) }
    }

    @Test
    fun `checkLoginState with no token should stay logout`() = runTest {
        // Arrange
        every { UserDao.getAccessToken() } returns null

        // Act
        val viewModel = AppViewModel(mockRepo).apply {
            checkLoginState()
            testDispatcher.scheduler.advanceUntilIdle()
        }

        // Assert
        assertFalse(viewModel.loginState.value.isLoggedIn)
        coVerify(exactly = 0) { mockRepo.getUserInfo(any()) }
    }

    @Test
    fun `logoff should clear all credentials`() = runTest {
        // Arrange
        val mockUser = mockk<User>().apply {
            every { id } returns "1"
            every { login } returns "testUser"
        }
        val viewModel = AppViewModel(mockRepo).apply {
            _loginState.value = LoginState(isLoggedIn = true, user = mockUser)
        }

        // Act
        viewModel.logoff()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertFalse(viewModel.loginState.value.isLoggedIn)
        assertEquals(null, viewModel.loginState.value.user)
        verify(exactly = 1) { UserDao.removeAccessToken() }
    }
}
