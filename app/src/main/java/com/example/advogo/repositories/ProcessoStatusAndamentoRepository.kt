package com.example.advogo.repositories

import com.example.advogo.models.ProcessoStatusAndamento
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoStatusAndamentoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoStatusAndamentoRepository {
    override fun ObterProcessoStatusAndamentos(onSuccessListener: (List<ProcessoStatusAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_ANDAMENTOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoStatusAndamento::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun ObterProcessoStatusAndamento(id: String, onSuccessListener: (processoStatusAndamentos: ProcessoStatusAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_ANDAMENTOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoStatusAndamentos = document.toObject(ProcessoStatusAndamento::class.java)
                    if (processoStatusAndamentos != null) {
                        onSuccessListener(processoStatusAndamentos)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun ObterProcessoStatusAndamentos(): List<ProcessoStatusAndamento>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_ANDAMENTOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoStatusAndamento::class.java)
                    continuation.resume(lista)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun ObterProcessoStatusAndamento(id: String): ProcessoStatusAndamento? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_ANDAMENTOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val statusAndamento = document.toObject(ProcessoStatusAndamento::class.java)!!
                    continuation.resume(statusAndamento)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}

interface IProcessoStatusAndamentoRepository {
    fun ObterProcessoStatusAndamentos(onSuccessListener: (lista: List<ProcessoStatusAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcessoStatusAndamento(id: String, onSuccessListener: (processoStatusAndamentos: ProcessoStatusAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun ObterProcessoStatusAndamentos(): List<ProcessoStatusAndamento>?
    suspend fun ObterProcessoStatusAndamento(id: String): ProcessoStatusAndamento?
}