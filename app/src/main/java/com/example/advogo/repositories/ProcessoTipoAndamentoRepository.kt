package com.example.advogo.repositories

import com.example.advogo.models.ProcessoTipoAndamento
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoTipoAndamentoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoTipoAndamentoRepository {
    override fun ObterProcessoTipoAndamentos(onSuccessListener: (List<ProcessoTipoAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_ANDAMENTOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoTipoAndamento::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun ObterProcessoTipoAndamento(id: String, onSuccessListener: (processoTipoAndamentos: ProcessoTipoAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_ANDAMENTOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoTipoAndamentos = document.toObject(ProcessoTipoAndamento::class.java)
                    if (processoTipoAndamentos != null) {
                        onSuccessListener(processoTipoAndamentos)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun ObterProcessoTipoAndamentos(): List<ProcessoTipoAndamento>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_ANDAMENTOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoTipoAndamento::class.java)
                    continuation.resume(lista)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun ObterProcessoTipoAndamento(id: String): ProcessoTipoAndamento? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_ANDAMENTOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val tipoAndamento = document.toObject(ProcessoTipoAndamento::class.java)!!
                    continuation.resume(tipoAndamento)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}

interface IProcessoTipoAndamentoRepository {
    fun ObterProcessoTipoAndamentos(onSuccessListener: (lista: List<ProcessoTipoAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcessoTipoAndamento(id: String, onSuccessListener: (processoTipoAndamentos: ProcessoTipoAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun ObterProcessoTipoAndamentos(): List<ProcessoTipoAndamento>?
    suspend fun ObterProcessoTipoAndamento(id: String): ProcessoTipoAndamento?
}