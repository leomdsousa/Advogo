package com.example.advogo.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anexo(
    @DocumentId
    var id: String = "",
    var nome: String? = null,
    var uri: String? = null,
    var descricao: String? = null,
    var data: String? = null,
    var dataTimestamp: Timestamp? = null,
    var advogado: String? = null,
    var processo: String? = null,
    @Transient var advogadoObj: Advogado? = null,
    @Transient var imagemSelecionadaURI: Uri? = null,
): Parcelable {
}