package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Processo(
    var id: String? = null,
    var numero: String? = null,
    var descricao: String? = null,
    var tipo: String? = null,
    var status: String? = null,
    var imagem: String? = null,
    var data: String? = null,
    var cliente: String? = null,
    var advogado: String? = null,
    var diligencias: List<String>? = null,
    var arquivos: List<String>? = null,
    @Transient var tipoObj: ProcessoTipo? = null,
    @Transient var statusObj: ProcessoStatus? = null,
    @Transient var clienteObj: Cliente? = null,
    @Transient var advogadoObj: Advogado? = null,
    @Transient var diligenciasLista: List<Diligencia>? = null,
    //@Transient var arquivosLista: List<Any>? = null,
): Parcelable {
}