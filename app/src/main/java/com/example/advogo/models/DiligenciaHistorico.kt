package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiligenciaHistorico(
    @DocumentId
    var id: String = "",
    var obs: String? = null,
    var advogado: String? = null,
    var status: String? = null,
    var tipo: String? = null,
    var data: String? = null,
    var dataTimestamp: Timestamp? = null,
    var diligencia: String? = null,
    @Exclude var advogadoObj: Advogado? = null,
    @Exclude var tipoObj: DiligenciaTipo? = null,
    @Exclude var statusObj: DiligenciaStatus? = null
): Parcelable {
}