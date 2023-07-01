package com.example.advogo.services

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract

class CalendarService {
    fun addEventToCalendar(
        contentResolver: ContentResolver,
        title: String, description: String,
        location: String,
        startTimeMillis: Long,
        endTimeMillis: Long
    ) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, 1) // ID do calendário. Pode variar de acordo com o dispositivo.
            put(CalendarContract.Events.TITLE, title)
            put(CalendarContract.Events.DESCRIPTION, description)
            put(CalendarContract.Events.EVENT_LOCATION, location)
            put(CalendarContract.Events.DTSTART, startTimeMillis)
            put(CalendarContract.Events.DTEND, endTimeMillis)
            put(CalendarContract.Events.EVENT_TIMEZONE, "GMT")
        }

        val uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }

    fun removeEventFromCalendar(contentResolver: ContentResolver, eventId: Long) {
        val deleteUri: Uri = Uri.withAppendedPath(CalendarContract.Events.CONTENT_URI, eventId.toString())
        contentResolver.delete(deleteUri, null, null)
    }
}