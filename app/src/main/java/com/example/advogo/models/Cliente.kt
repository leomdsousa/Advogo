package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cliente(
    var id: String? = null,
    var nome: String? = null,
    var cpf: String,
    var email: String,
    var endereco: String,
    var enderecoLat: Long,
    var enderecoLong: Long,
    var telefone: String? = null,
    @Transient var telefoneObj: Telefone? = null
): Parcelable {
}