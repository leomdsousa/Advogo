package com.example.advogo.repositories

import com.example.advogo.models.ProcessoTipo
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProcessoTipoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoTipoRepository {
    override fun ObterProcessosTipos(onSuccessListener: (List<ProcessoTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoTipo::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun ObterProcessoTipo(id: String, onSuccessListener: (processoTipo: ProcessoTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .whereEqualTo(Constants.PROCESSOS_TIPOS_ID, id)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val processoTipo = document.first().toObject(ProcessoTipo::class.java)
                    onSuccessListener(processoTipo)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
}

interface IProcessoTipoRepository {
    fun ObterProcessosTipos(onSuccessListener: (lista: List<ProcessoTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcessoTipo(id: String, onSuccessListener: (processoTipo: ProcessoTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}