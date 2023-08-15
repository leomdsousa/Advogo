package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cliente(
    @DocumentId
    var id: String = "",
    var nome: String? = null,
    var rg: String? = null,
    var cpf: String? = null,
    var data: String? = null,
    var estadoCivil: String? = null,
    var email: String? = null,
    var endereco: String? = null,
    var enderecoNumero: String? = null,
    var enderecoCidade: String? = null,
    var enderecoBairro: String? = null,
    var telefone: String? = null,
    var whatsapp: Boolean? = false,
    @Transient var telefoneObj: Telefone? = null,
    @Transient var selecionado: Boolean = false
): Parcelable {
}