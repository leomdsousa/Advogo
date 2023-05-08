package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Processo(
    var id: String? = null,
    var numero: String? = null,
    var descricao: String? = null,
    var tipo: Long? = null,
    var status: Long? = null,
    var imagem: String? = null,
    var data: Date? = null,
    var cliente: Cliente? = null,
    var advogado: Advogado? = null,
    var diligencias: List<Diligencia>? = null
): Parcelable {
}