package com.example.advogo.utils.extensions

import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.O)
object DataUtils {
    fun onDatePickerResult(ano: Int, mes: Int, dia: Int): OnDatePickerResultModel {
        val sDayOfMonth = if (dia < 10) "0$dia" else "$dia"
        val sMonthOfYear = if ((mes + 1) < 10) "0${mes + 1}" else "${mes + 1}"

        return OnDatePickerResultModel(
            "$ano-$sMonthOfYear-$sDayOfMonth",
            "$sDayOfMonth/$sMonthOfYear/$ano"
        )
    }
}

class OnDatePickerResultModel(
    var dataUSA: String,
    var dataBR: String
    ) { }