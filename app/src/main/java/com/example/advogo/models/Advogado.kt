package com.example.advogo.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Advogado(
    var id: String? = null,
    var nome: String? = null,
    var email: String? = null,
    var endereco: String? = null,
    var image: String? = null,
    var oab: String? = null,
    var telefone: String? = null
): Parcelable

