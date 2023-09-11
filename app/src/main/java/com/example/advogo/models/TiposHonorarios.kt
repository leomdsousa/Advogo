package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class TiposHonorarios(
    @DocumentId
    var id: String = "",
    var nome: String,
    var descricao: String = "",
    var valor: Float,
    var termosContratuais: String = "",
    var observacoes: String = "",
    var ativo: Boolean? = true,
): Parcelable
