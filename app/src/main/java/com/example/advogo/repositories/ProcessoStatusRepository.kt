package com.example.advogo.repositories

import com.example.advogo.models.ProcessoStatus
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProcessoStatusRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoStatusRepository {
    override fun ObterProcessosStatuss(onSuccessListener: (List<ProcessoStatus>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoStatus::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun ObterProcessoStatus(id: String, onSuccessListener: (processoStatus: ProcessoStatus) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoStatus = document.toObject(ProcessoStatus::class.java)
                    if (processoStatus != null) {
                        onSuccessListener(processoStatus)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
}

interface IProcessoStatusRepository {
    fun ObterProcessosStatuss(onSuccessListener: (lista: List<ProcessoStatus>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcessoStatus(id: String, onSuccessListener: (processoStatus: ProcessoStatus) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}