package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
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
    @Transient var processoObj: Processo? = null,
    @Transient var advogadoObj: Advogado? = null,
    @Transient var tipoObj: DiligenciaTipo? = null,
    @Transient var statusObj: DiligenciaStatus? = null,
    @Transient var historicoLista: List<DiligenciaHistorico>? = arrayListOf()
): Parcelable {
}