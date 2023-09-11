package com.example.advogo.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Processo(
    @DocumentId
    var id: String = "",
    var titulo: String? = null,
    var numero: String? = null,
    var descricao: String? = null,
    var tipo: String? = null,
    var status: String? = null,
    var imagem: String? = null,
    var dataInicio: String? = null,
    var dataInicioTimestamp: Timestamp? = null,
    var dataTermino: String? = null,
    var dataTerminoTimestamp: Timestamp? = null,
    var dataCriacao: String? = null,
    var dataCriacaoTimestamp: Timestamp? = null,
    var dataAlteracao: String? = null,
    var dataAlteracaoTimestamp: Timestamp? = null,
    var cliente: String? = null,
    var advogado: String? = null,
    var diligencias: List<String>? = null,
    var anexos: List<String>? = null,
    var andamentos: List<String>? = null,
    var historico: List<String>? = null,
    var partes: List<ProcessoParte> = listOf(),
    @Transient var tipoObj: ProcessoTipo? = null,
    @Transient var statusObj: ProcessoStatus? = null,
    @Transient var clienteObj: Cliente? = null,
    @Transient var advogadoObj: Advogado? = null,
    @Transient var diligenciasLista: List<Diligencia>? = null,
    @Transient var anexosLista: List<Anexo>? = null,
    @Transient var andamentosLista: List<ProcessoAndamento>? = null,
    @Transient var historicoLista: List<ProcessoHistorico>? = null,
    @Transient var selecionado: Boolean = false,
    @Transient var imagemSelecionadaURI: Uri? = null,
): Parcelable {
}