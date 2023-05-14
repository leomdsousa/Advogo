package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cliente(
    var id: String? = null,
    var nome: String? = null,
    var cpf: String? = null,
    var email: String? = null,
    var endereco: String? = null,
    var enderecoNumero: String? = null,
    var enderecoCidade: String? = null,
    var enderecoBairro: String? = null,
    var telefone: String? = null,
    @Transient var telefoneObj: Telefone? = null
): Parcelable {
}