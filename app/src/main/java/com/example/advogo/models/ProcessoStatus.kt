package com.example.advogo.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessoStatus(
    @DocumentId
    var id: String = "",
    var status: String? = null
): Parcelable {
}