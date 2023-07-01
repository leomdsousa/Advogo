package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.models.Telefone
import com.example.advogo.models.TelefoneTipo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject

class TelefoneTipoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): ITelefoneTipoRepository {
    override fun obterTelefonesTipos(onSuccessListener: (List<TelefoneTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TELEFONES_TIPOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(TelefoneTipo::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun obterTelefoneTipo(id: String, onSuccessListener: (telefoneTipo: TelefoneTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TELEFONES_TIPOS_TABLE)
            .whereEqualTo(Constants.TELEFONES_TIPOS_ID, id)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val telefoneTipo = document.first().toObject(TelefoneTipo::class.java)
                    onSuccessListener(telefoneTipo)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
}

interface ITelefoneTipoRepository {
    fun obterTelefonesTipos(onSuccessListener: (lista: List<TelefoneTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterTelefoneTipo(id: String, onSuccessListener: (telefoneTipo: TelefoneTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}