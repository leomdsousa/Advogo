package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Telefone(
    var numero: String? = null
): Parcelable {
}