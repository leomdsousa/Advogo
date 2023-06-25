package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Advogado(
    @DocumentId
    var id: String = "",
    var nome: String? = null,
    var sobrenome: String? = null,
    var email: String? = null,
    var endereco: String? = null,
    var enderecoLat: Long? = null,
    var enderecoLong: Long? = null,
    var imagem: String? = null,
    var oab: Long? = null,
    var telefone: String? = null,
    var fcmToken: String? = null,
    var whatsapp: Boolean? = null,
    @Transient var telefoneObj: Telefone? = null,
    @Transient var selecionado: Boolean = false
): Parcelable { }

