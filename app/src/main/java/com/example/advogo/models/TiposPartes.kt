package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class TiposPartes(
    @DocumentId
    var id: String = "",
    var tipo: String? = null,
    var ativo: Boolean? = true,
    @Transient var selecionado: Boolean = false
): Parcelable
