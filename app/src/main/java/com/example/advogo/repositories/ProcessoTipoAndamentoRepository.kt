package com.example.advogo.repositories

import com.example.advogo.models.ProcessoTipoAndamento
import com.example.advogo.utils.constants.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoTipoAndamentoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoTipoAndamentoRepository {
    override fun obterProcessoTipoAndamentos(onSuccessListener: (List<ProcessoTipoAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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
    override fun obterProcessoTipoAndamento(id: String, onSuccessListener: (processoTipoAndamentos: ProcessoTipoAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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

    override suspend fun obterProcessoTipoAndamentos(): List<ProcessoTipoAndamento>? = suspendCoroutine { continuation ->
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
    override suspend fun obterProcessoTipoAndamento(id: String): ProcessoTipoAndamento? = suspendCoroutine { continuation ->
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

    override fun adicionarProcessoTipoAndamento(model: ProcessoTipoAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_ANDAMENTOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarProcessoTipoAndamento(model: ProcessoTipoAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_ANDAMENTOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarProcessoTipoAndamento(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_ANDAMENTOS_TABLE)
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

interface IProcessoTipoAndamentoRepository {
    fun obterProcessoTipoAndamentos(onSuccessListener: (lista: List<ProcessoTipoAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessoTipoAndamento(id: String, onSuccessListener: (processoTipoAndamentos: ProcessoTipoAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessoTipoAndamentos(): List<ProcessoTipoAndamento>?
    suspend fun obterProcessoTipoAndamento(id: String): ProcessoTipoAndamento?

    fun adicionarProcessoTipoAndamento(model: ProcessoTipoAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarProcessoTipoAndamento(model: ProcessoTipoAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarProcessoTipoAndamento(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}