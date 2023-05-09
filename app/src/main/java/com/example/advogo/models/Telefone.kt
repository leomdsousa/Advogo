package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Telefone(
    var id: String? = null,
    var numero: String? = null,
    var tipo: String? = null,
    @Transient var tipoObj: TelefoneTipo? = null
): Parcelable {
}