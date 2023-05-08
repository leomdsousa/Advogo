package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cliente(
    var id: String? = null,
    var nome: String? = null,
    var cpf: String,
    var endereco: String,
    var telefones: List<Telefone>
): Parcelable {
}