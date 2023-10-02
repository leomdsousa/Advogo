package com.example.advogo.utils.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object DateExtensions {
    @RequiresApi(Build.VERSION_CODES.O)
    fun LocalDate.fromLocalDateToDateString(): String {
        return "${this.year}-${this.monthValue.toString().padStart(2,'0')}-${this.dayOfMonth.toString().padStart(2,'0')}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun CalendarDay.fromCalendarDayToDateString(): String {
        return "${this.year}-${this.month.toString().padStart(2, '0')}-${this.day.toString().padStart(2, '0')}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Date.fromDateToDateString(): String {
        return "${this.year}-${this.month.toString().padStart(2,'0')}-${this.day.toString().padStart(2,'0')}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Date.fromDateToBrDateString(): String {
        val localDate: LocalDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return "${localDate.dayOfMonth.toString().padStart(2,'0')}/${(localDate.monthValue).toString().padStart(2,'0')}/${localDate.year}"
    }
}


