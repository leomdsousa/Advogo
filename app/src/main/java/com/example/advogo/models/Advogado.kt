package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Advogado(
    var id: String? = null,
    var nome: String? = null,
    var sobrenome: String? = null,
    var email: String? = null,
    var endereco: String? = null,
    var enderecoLat: Long? = null,
    var enderecoLong: Long? = null,
    var imagem: String? = null,
    var oab: Long? = null,
    var telefone: String? = null,
    //TODO("Validar")
    var selecionado: Boolean = false
): Parcelable {
    @IgnoredOnParcel
    val selecionado_2: Boolean = selecionado
}

