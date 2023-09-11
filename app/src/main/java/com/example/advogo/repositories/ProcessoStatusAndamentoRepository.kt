package com.example.advogo.repositories

import com.example.advogo.models.ProcessoStatusAndamento
import com.example.advogo.utils.constants.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoStatusAndamentoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoStatusAndamentoRepository {
    override fun obterProcessoStatusAndamentos(onSuccessListener: (List<ProcessoStatusAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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
    override fun obterProcessoStatusAndamento(id: String, onSuccessListener: (processoStatusAndamentos: ProcessoStatusAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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

    override suspend fun obterProcessoStatusAndamentos(): List<ProcessoStatusAndamento>? = suspendCoroutine { continuation ->
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
    override suspend fun obterProcessoStatusAndamento(id: String): ProcessoStatusAndamento? = suspendCoroutine { continuation ->
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

    override fun adicionarProcessoStatusAndamento(model: ProcessoStatusAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_ANDAMENTOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarProcessoStatusAndamento(model: ProcessoStatusAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_ANDAMENTOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarProcessoStatusAndamento(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_ANDAMENTOS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
}

interface IProcessoStatusAndamentoRepository {
    fun obterProcessoStatusAndamentos(onSuccessListener: (lista: List<ProcessoStatusAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessoStatusAndamento(id: String, onSuccessListener: (processoStatusAndamentos: ProcessoStatusAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessoStatusAndamentos(): List<ProcessoStatusAndamento>?
    suspend fun obterProcessoStatusAndamento(id: String): ProcessoStatusAndamento?
    fun adicionarProcessoStatusAndamento(model: ProcessoStatusAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarProcessoStatusAndamento(model: ProcessoStatusAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarProcessoStatusAndamento(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}