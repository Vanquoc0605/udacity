package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var isErrorInternal = true
    private var listReminder: MutableList<ReminderDTO> = mutableListOf()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (!isErrorInternal) {
            return Result.Error("reminders not found!")
        }
        return Result.Success(listReminder)
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        listReminder.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (!isErrorInternal) return Result.Error("Internal error")
        val getReminderDetail = listReminder.find { it.id == id }
        if (getReminderDetail != null) {
            return Result.Success(getReminderDetail)
        }
        return Result.Error("reminder not found!")
    }

    override suspend fun deleteAllReminders() {
        listReminder.clear()
    }


}