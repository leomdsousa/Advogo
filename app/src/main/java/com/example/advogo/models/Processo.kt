package com.example.advogo.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
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
    @Exclude var tipoObj: ProcessoTipo? = null,
    @Exclude var statusObj: ProcessoStatus? = null,
    @Exclude var clienteObj: Cliente? = null,
    @Exclude var advogadoObj: Advogado? = null,
    @Exclude var diligenciasLista: List<Diligencia>? = null,
    @Exclude var anexosLista: List<Anexo>? = null,
    @Exclude var andamentosLista: List<ProcessoAndamento>? = null,
    @Exclude var historicoLista: List<ProcessoHistorico>? = null,
    @Exclude var selecionado: Boolean? = null,
    @Exclude var imagemSelecionadaURI: Uri? = null,
): Parcelable {

}