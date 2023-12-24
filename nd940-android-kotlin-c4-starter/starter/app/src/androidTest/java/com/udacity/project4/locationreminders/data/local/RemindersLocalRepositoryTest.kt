package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private lateinit var reminderDao: RemindersDao
    private lateinit var remindersLocalRepository: RemindersLocalRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        reminderDao = database.reminderDao()
        remindersLocalRepository = RemindersLocalRepository(reminderDao, Dispatchers.Unconfined)
    }


    @Test
    fun testInsertRemindersSuccess() = runBlockingTest {
        val reminder = ReminderDTO(
            "titleG", "desG", "locationG", 10.0, 10.0
        )
        val reminder1 = ReminderDTO("title B", "desB", "locationB", 10.0, 10.0)
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.saveReminder(reminder1)
        val actual = remindersLocalRepository.getReminder(reminder.id)
        val actual1 = remindersLocalRepository.getReminder(reminder1.id)
        assertThat(
            actual, `is`(Result.Success(reminder))
        )
        assertThat(
            actual1, `is`(Result.Success(reminder1))
        )
    }

    @Test
    fun testGetNotFoundReminderReturnError() = runBlocking {
        assertThat(
            remindersLocalRepository.getReminder("test"),
            `is`(Result.Error("Reminder not found!"))
        )
    }

}