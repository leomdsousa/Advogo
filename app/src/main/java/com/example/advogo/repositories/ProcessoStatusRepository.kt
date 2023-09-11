package com.example.advogo.repositories

import com.example.advogo.models.ProcessoStatus
import com.example.advogo.utils.constants.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoStatusRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoStatusRepository {
    override fun obterProcessosStatus(onSuccessListener: (List<ProcessoStatus>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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
    override fun obterProcessoStatus(id: String, onSuccessListener: (processoStatus: ProcessoStatus) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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

    override suspend fun obterProcessoStatus(): List<ProcessoStatus>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoStatus::class.java)
                    continuation.resume(lista)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterProcessoStatus(id: String): ProcessoStatus? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val status = document.toObject(ProcessoStatus::class.java)!!
                    continuation.resume(status)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override fun adicionarProcessoStatus(model: ProcessoStatus, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarProcessoStatus(model: ProcessoStatus, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarProcessoStatus(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_STATUS_TABLE)
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

interface IProcessoStatusRepository {
    fun obterProcessosStatus(onSuccessListener: (lista: List<ProcessoStatus>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessoStatus(id: String, onSuccessListener: (processoStatus: ProcessoStatus) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessoStatus(): List<ProcessoStatus>?
    suspend fun obterProcessoStatus(id: String): ProcessoStatus?

    fun adicionarProcessoStatus(model: ProcessoStatus, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarProcessoStatus(model: ProcessoStatus, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarProcessoStatus(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}