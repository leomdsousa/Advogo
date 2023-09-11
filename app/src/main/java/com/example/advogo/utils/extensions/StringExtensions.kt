package com.example.advogo.utils.extensions

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.util.*

object StringExtensions {
    fun String.removeSpecialCharacters(): String {
        return this.replace("[^a-zA-Z0-9 ]".toRegex(), "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.fromUSADateStringToLocalDate(): LocalDate {
        return LocalDate.of(
            this.substring(0,4).padStart(4,'0').toInt(),
            Month.of(this.substring(6,7).padStart(2,'0').toInt()),
            this.substring(8,10).padStart(2,'0').toInt()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.fromUSADateStringToLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(
            this.substring(0,4).padStart(4,'0').toInt(),
            Month.of(this.substring(6,7).padStart(2,'0').toInt()),
            this.substring(8,10).padStart(2,'0').toInt(),
            this.substring(12,13).padStart(2,'0').toInt(),
            this.substring(15,16).padStart(2,'0').toInt(),
            this.substring(18,19).padStart(2,'0').toInt()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.fromUSADateStringToDate(): Date {
        val cal = Calendar.getInstance()
        cal.set(
            this.substring(0,4).padStart(4,'0').toInt(),
            this.substring(6,7).padStart(2,'0').toInt()-1,
            this.substring(8,10).padStart(2,'0').toInt()
        )
        return cal.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.fromUSADateStringToDateWithTime(): Date {
        val cal = Calendar.getInstance()
        cal.set(
            this.substring(0,4).padStart(4,'0').toInt(),
            this.substring(6,7).padStart(2,'0').toInt()-1,
            this.substring(8,10).padStart(2,'0').toInt(),
            this.substring(12,13).padStart(2,'0').toInt(),
            this.substring(15,16).padStart(2,'0').toInt(),
            0,
        )
        return cal.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.fromUSADateStringToTimestamp(): Timestamp {
        return Timestamp(Date(this))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.fromUSADateTimeStringToTimestamp(): Timestamp {
        return Timestamp(Date(this))
    }

}