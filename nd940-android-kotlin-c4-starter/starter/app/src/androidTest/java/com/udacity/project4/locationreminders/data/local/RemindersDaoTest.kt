package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private val listReminderDataTest = mutableListOf(
        ReminderDTO("title A", "desA", "locationA", 10.0, 10.0),
        ReminderDTO("title B", "desB", "locationB", 10.0, 10.0),
        ReminderDTO("title C", "desC", "locationC", 10.0, 10.0),
        ReminderDTO("title D", "desD", "locationD", 10.0, 10.0),
        ReminderDTO("title E", "desE", "locationE", 10.0, 10.0),
        ReminderDTO("title F", "desF", "locationF", 10.0, 10.0)
    )

    private lateinit var reminderDao: RemindersDao
    private lateinit var database: RemindersDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        reminderDao = database.reminderDao()
        insertAllRemind()
    }


    private fun insertAllRemind() {
        runBlocking {
            reminderDao.saveReminder(listReminderDataTest[0])
            reminderDao.saveReminder(listReminderDataTest[1])
            reminderDao.saveReminder(listReminderDataTest[2])
            reminderDao.saveReminder(listReminderDataTest[3])
            reminderDao.saveReminder(listReminderDataTest[4])
            reminderDao.saveReminder(listReminderDataTest[5])
        }
    }

    @Test
    fun saveRemindSuccess() = runBlocking {
        val listReminderDTO = listOf(
            ReminderDTO("title G", "desG", "locationG", 10.0, 10.0),
            ReminderDTO("title H", "desH", "locationH", 10.0, 10.0),
        )

        database.reminderDao().saveReminder(listReminderDTO[0])
        database.reminderDao().saveReminder(listReminderDTO[1])
        val list = database.reminderDao().getReminders()

        assertThat(list.size, `is`(8))
        Assert.assertEquals(list[6], listReminderDTO[0])
        Assert.assertEquals(list[7], listReminderDTO[1])
    }

    @Test
    fun testDeleteAll() = runBlocking {
        reminderDao.deleteAllReminders()
        val actual = reminderDao.getReminders()
        assertThat(actual, `is`(emptyList()))
    }

}