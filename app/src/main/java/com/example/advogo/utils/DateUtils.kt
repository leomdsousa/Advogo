package com.example.advogo.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object DateUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateFinalDate(initialDate: Date, qtdDays: Int, onlyWorkingDays: Boolean): Date {
        val initialDate = initialDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

        val finalDate = if (onlyWorkingDays) {
            calculateFinalDateJustWorkingDays(initialDate, qtdDays)
        } else {
            initialDate.plusDays(qtdDays.toLong())
        }

        return Date.from(finalDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateFinalDateJustWorkingDays(date: LocalDate, qtdDays: Int): LocalDate {
        var calculatedDay = date
        var daysLeft = qtdDays

        while (daysLeft > 0) {
            calculatedDay = calculatedDay.plusDays(1)
            if (isWorkingDay(calculatedDay)) {
                daysLeft--
            }
        }

        return calculatedDay
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isWorkingDay(date: LocalDate): Boolean {
        val weekDay = date.dayOfWeek
        return weekDay != DayOfWeek.SATURDAY && weekDay != DayOfWeek.SUNDAY
    }
}