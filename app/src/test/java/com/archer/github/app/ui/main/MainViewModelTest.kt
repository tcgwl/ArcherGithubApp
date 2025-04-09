package com.archer.github.app.ui.main

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        viewModel = MainViewModel()
    }

    @Test
    fun `initial selectedTab should be 0`() {
        assertEquals(0, viewModel.selectedTab.value)
    }

    @Test
    fun `selectTab should update selectedTab value`() {
        viewModel.selectTab(2)
        assertEquals(2, viewModel.selectedTab.value)
    }

    @Test
    fun `selectTab multiple times should reflect latest state`() {
        viewModel.selectTab(1)
        assertEquals(1, viewModel.selectedTab.value)

        viewModel.selectTab(3)
        assertEquals(3, viewModel.selectedTab.value)

        viewModel.selectTab(0)
        assertEquals(0, viewModel.selectedTab.value)
    }
}
