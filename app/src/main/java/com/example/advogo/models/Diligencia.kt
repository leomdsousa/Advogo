package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class Diligencia(
    var id: String? = null,
    var descricao: String? = null,
    var data: Date? = null,
    var processo: Processo? = null,
    var endereco: String? = null
): Parcelable {
}