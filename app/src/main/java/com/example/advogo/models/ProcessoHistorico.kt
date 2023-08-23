package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessoHistorico(
    @DocumentId
    var id: String = "",
    var obs: String? = null,
    var advogado: String? = null,
    var status: String? = null,
    var tipo: String? = null,
    var data: String? = null,
    var dataTimestamp: Timestamp? = null,
    var processo: String? = null,
    @Transient var advogadoObj: Advogado? = null,
    @Transient var tipoObj: ProcessoTipo? = null,
    @Transient var statusObj: ProcessoStatus? = null
): Parcelable {
}