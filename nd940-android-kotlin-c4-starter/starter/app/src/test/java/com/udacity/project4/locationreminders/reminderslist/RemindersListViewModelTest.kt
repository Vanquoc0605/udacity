package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var dataSource: FakeDataSource

    private lateinit var viewModel: RemindersListViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun loadReminders_success() = runBlocking {
        val reminder = ReminderDTO("1", "Title 1", "Description 1", 0.0, 0.0, "1")
        dataSource.saveReminder(reminder)


        viewModel.loadReminders()

        assertEquals(false, viewModel.showLoading.value)
        assertEquals(false, viewModel.showNoData.value)
        assertEquals(1, viewModel.remindersList.value?.size)
        assertEquals(
            listOf(
                ReminderDataItem("1", "Title 1", "Description 1", 0.0, 0.0, "1")
            ),
            viewModel.remindersList.value
        )
    }

    @Test
    fun loadReminders_fail() = runBlocking {
        dataSource.shouldReturnError = true
        viewModel.loadReminders()

        assertEquals("Data not found!", viewModel.showSnackBar.value)
        assertEquals(true, viewModel.showNoData.value)
        assertEquals(null, viewModel.remindersList.value?.size)
    }

    @After
    fun cleanup() {
        stopKoin()
    }
}