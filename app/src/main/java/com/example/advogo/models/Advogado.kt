package com.example.advogo.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Advogado(
    @DocumentId
    var id: String = "",
    var nome: String? = null,
    var sobrenome: String? = null,
    var email: String? = null,
    var dataCriacao: String? = null,
    var dataCriacaoTimestamp: Timestamp? = null,
    var dataAlteracao: String? = null,
    var dataAlteracaoTimestamp: Timestamp? = null,
    var endereco: String? = null,
    var enderecoLat: Double? = null,
    var enderecoLong: Double? = null,
    var imagem: String? = null,
    var oab: Long? = null,
    var telefone: String? = null,
    var fcmToken: String? = null,
    var whatsapp: Boolean? = null,
    @Exclude var telefoneObj: Telefone? = null,
    @Exclude var selecionado: Boolean? = null,
    @Exclude var imagemSelecionadaURI: Uri? = null,
    @Exclude var username: String? = null,
    @Exclude var password: String? = null,
): Parcelable { }

