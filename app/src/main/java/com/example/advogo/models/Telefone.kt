package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Telefone(
    var id: Long? = null,
    var numero: String? = null,
    var tipo: Long? = null
): Parcelable {
}