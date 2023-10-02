package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
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
    @Exclude var advogadoObj: Advogado? = null,
    @Exclude var tipoObj: ProcessoTipoAndamento? = null,
    @Exclude var statusObj: ProcessoStatusAndamento? = null,
    @Exclude var processoObj: Processo? = null,
    @Exclude var dataPrazo: String? = null,
): Parcelable {
}