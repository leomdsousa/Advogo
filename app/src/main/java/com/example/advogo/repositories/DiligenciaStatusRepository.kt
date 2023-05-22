package com.example.advogo.repositories

import com.example.advogo.models.DiligenciaStatus
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiligenciaStatusRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IDiligenciaStatusRepository {
    override fun ObterDiligenciasStatus(onSuccessListener: (List<DiligenciaStatus>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_STATUS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(DiligenciaStatus::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun ObterDiligenciaStatus(id: String, onSuccessListener: (processoStatus: DiligenciaStatus) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_STATUS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoStatus = document.toObject(DiligenciaStatus::class.java)
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

    override suspend fun ObterDiligenciasStatus(): List<DiligenciaStatus>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_STATUS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(DiligenciaStatus::class.java)
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun ObterDiligenciaStatus(id: String): DiligenciaStatus? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_STATUS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val status = document.toObject(DiligenciaStatus::class.java)!!
                    continuation.resume(status)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}

interface IDiligenciaStatusRepository {
    fun ObterDiligenciasStatus(onSuccessListener: (lista: List<DiligenciaStatus>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligenciaStatus(id: String, onSuccessListener: (processoStatus: DiligenciaStatus) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun ObterDiligenciasStatus(): List<DiligenciaStatus>?
    suspend fun ObterDiligenciaStatus(id: String): DiligenciaStatus?
}