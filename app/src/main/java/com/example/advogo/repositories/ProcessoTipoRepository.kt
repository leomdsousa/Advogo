package com.example.advogo.repositories

import com.example.advogo.models.ProcessoTipo
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoTipo = document.toObject(ProcessoTipo::class.java)
                    if (processoTipo != null) {
                        onSuccessListener(processoTipo)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun ObterProcessosTipos(): List<ProcessoTipo>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(ProcessoTipo::class.java)
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun ObterProcessoTipo(id: String): ProcessoTipo? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(ProcessoTipo::class.java)!!
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}

interface IProcessoTipoRepository {
    fun ObterProcessosTipos(onSuccessListener: (lista: List<ProcessoTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcessoTipo(id: String, onSuccessListener: (processoTipo: ProcessoTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun ObterProcessosTipos(): List<ProcessoTipo>?
    suspend fun ObterProcessoTipo(id: String): ProcessoTipo?
}