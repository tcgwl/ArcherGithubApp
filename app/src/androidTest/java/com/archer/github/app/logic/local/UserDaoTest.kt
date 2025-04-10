package com.archer.github.app.logic.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserDaoTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        UserDao.contextProvider = { context }

        UserDao.removeAccessToken()
    }

    @After
    fun tearDown() {
        UserDao.removeAccessToken()
    }

    @Test
    fun testSaveAccessToken() {
        val token = "test_token_123"
        UserDao.saveAccessToken(token)

        val savedToken = UserDao.getAccessToken()
        assertEquals(token, savedToken)
    }

    @Test
    fun testRemoveAccessToken() {
        val token = "test_token_456"
        UserDao.saveAccessToken(token)

        assertEquals(token, UserDao.getAccessToken())

        UserDao.removeAccessToken()
        assertNull(UserDao.getAccessToken())
    }
}
