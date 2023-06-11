package com.example.advogo.repositories

import com.example.advogo.models.ProcessoAndamento
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoAndamentoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoAndamentoRepository {
    override fun ObterProcessosAndamentos(onSuccessListener: (List<ProcessoAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoAndamento::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun ObterProcessoAndamento(id: String, onSuccessListener: (ProcessoAndamento: ProcessoAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val ProcessoAndamento = document.toObject(ProcessoAndamento::class.java)
                    if (ProcessoAndamento != null) {
                        onSuccessListener(ProcessoAndamento)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun ObterProcessosAndamentos(): List<ProcessoAndamento>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(ProcessoAndamento::class.java)
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun ObterProcessoAndamento(id: String): ProcessoAndamento? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(ProcessoAndamento::class.java)!!
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

interface IProcessoAndamentoRepository {
    fun ObterProcessosAndamentos(onSuccessListener: (lista: List<ProcessoAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcessoAndamento(id: String, onSuccessListener: (ProcessoAndamento: ProcessoAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun ObterProcessosAndamentos(): List<ProcessoAndamento>?
    suspend fun ObterProcessoAndamento(id: String): ProcessoAndamento?
}