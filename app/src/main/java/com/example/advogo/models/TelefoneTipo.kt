package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TelefoneTipo(
    var id: String? = null,
    var tipo: String? = null
): Parcelable {
}