package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Diligencia(
    var id: String? = null,
    var descricao: String? = null,
    var data: String? = null,
    var status: String? = null,
    var tipo: String? = null,
    var endereco: String? = null,
    var enderecoLat: Long? = null,
    var enderecoLong: Long? = null,
    var processo: String? = null,
    var advogado: String? = null,
    @Transient var processoObj: Processo? = null,
    @Transient var advogadoObj: Advogado? = null,
    @Transient var tipoObj: DiligenciaTipo? = null,
    @Transient var statusObj: DiligenciaStatus? = null
): Parcelable {
}