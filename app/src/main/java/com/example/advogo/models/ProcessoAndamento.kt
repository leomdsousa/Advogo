package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessoAndamento(
    @DocumentId
    var id: String = "",
    var descricao: String? = null,
    var advogado: String? = null,
    var processo: String? = null,
    var tipo: String? = null,
    var status: String? = null,
    var data: String? = null,
    var dataTimestamp: Timestamp? = null,
    @Transient var advogadoObj: Advogado? = null,
    @Transient var tipoObj: ProcessoTipoAndamento? = null,
    @Transient var statusObj: ProcessoStatusAndamento? = null,
    @Transient var processoObj: Processo? = null,
): Parcelable {
}