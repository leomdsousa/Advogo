package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Telefone(
    @DocumentId
    var id: String = "",
    var numero: String? = null,
    var tipo: String? = null,
    @Transient var tipoObj: TelefoneTipo? = null
): Parcelable {
}