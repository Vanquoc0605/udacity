package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var dataSource: FakeDataSource

    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun saveReminders_fail_empty_title() = runBlockingTest {
        val reminder = ReminderDataItem("", "Title 1", "Description 1", 0.0, 0.0, "1")

        viewModel.validateAndSaveReminder(reminder)

        Assert.assertEquals(R.string.err_enter_title, viewModel.showSnackBarInt.value)
    }

    @Test
    fun saveReminders_fail_empty_location() = runBlockingTest {
        val reminder = ReminderDataItem("Title", "description 1", "", 0.0, 0.0, "1")

        viewModel.validateAndSaveReminder(reminder)

        Assert.assertEquals(R.string.err_select_location, viewModel.showSnackBarInt.value)
    }

    @Test
    fun saveReminders_success() = runBlockingTest {
        val reminder = ReminderDataItem("Title", "description 1", "location", 0.0, 0.0, "1")

        viewModel.validateAndSaveReminder(reminder)

        Assert.assertEquals("Reminder Saved !", viewModel.showToast.value)
        Assert.assertEquals(
            Result.Success(
                mutableListOf(
                    ReminderDTO(
                        "Title",
                        "description 1",
                        "location",
                        0.0,
                        0.0,
                        "1"
                    )
                )
            ), dataSource.getReminders()
        )
    }


    @After
    fun cleanup() {
        stopKoin()
    }
}

