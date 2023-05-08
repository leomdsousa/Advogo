package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessoTipo(
    var id: Long? = null,
    var tipo: String? = null
): Parcelable {
}