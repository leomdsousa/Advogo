package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessoStatus(
    var id: Long? = null,
    var status: String? = null
): Parcelable {
}