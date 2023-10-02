package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Diligencia(
    @DocumentId
    var id: String = "",
    var descricao: String? = null,
    var data: String? = null,
    var dataTimestamp: Timestamp? = null,
    var dataCriacao: String? = null,
    var dataCriacaoTimestamp: Timestamp? = null,
    var dataAlteracao: String? = null,
    var dataAlteracaoTimestamp: Timestamp? = null,
    var status: String? = null,
    var tipo: String? = null,
    var endereco: String? = null,
    var enderecoLat: Double? = null,
    var enderecoLong: Double? = null,
    var processo: String? = null,
    var advogado: String? = null,
    var historico: List<String>? = null,
    @Exclude var processoObj: Processo? = null,
    @Exclude var advogadoObj: Advogado? = null,
    @Exclude var tipoObj: DiligenciaTipo? = null,
    @Exclude var statusObj: DiligenciaStatus? = null,
    @Exclude var historicoLista: List<DiligenciaHistorico>? = arrayListOf()
): Parcelable {
}