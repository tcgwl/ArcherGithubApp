package com.archer.github.app.logic.local

import android.content.Context
import android.content.SharedPreferences
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserDaoTest {

    private lateinit var context: Context
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        sharedPreferences = mockk()
        editor = mockk()

        UserDao.contextProvider = { context }

        every { sharedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor
        every { editor.remove(any()) } returns editor
        every { editor.apply() } just Runs
        every { sharedPreferences.getString("access_token", null) } returns "mocked_token"

        UserDao.preferencesProvider = { sharedPreferences }
    }

    @After
    fun tearDown() {
        UserDao.preferencesProvider = null
        unmockkAll()
    }

    @Test
    fun `saveAccessToken should call putString on SharedPreferences`() {
        val token = "test_token_123"

        UserDao.saveAccessToken(token)

        verify {
            editor.putString("access_token", token)
            editor.apply()
        }
    }

    @Test
    fun `getAccessToken should return stored token`() {
        every { sharedPreferences.getString("access_token", null) } returns "stored_token"

        val token = UserDao.getAccessToken()

        assertEquals("stored_token", token)
    }

    @Test
    fun `getAccessToken should return null when not set`() {
        every { sharedPreferences.getString("access_token", null) } returns null

        val token = UserDao.getAccessToken()

        assertNull(token)
    }

    @Test
    fun `removeAccessToken should call remove on SharedPreferences`() {
        UserDao.removeAccessToken()

        verify {
            editor.remove("access_token")
            editor.apply()
        }
    }
}
