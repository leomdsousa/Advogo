package com.example.advogo.repositories

import com.example.advogo.models.DiligenciaHistorico
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiligenciaHistoricoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IDiligenciaHistoricoRepository {
    override fun obterDiligenciasHistoricos(onSuccessListener: (List<DiligenciaHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(DiligenciaHistorico::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterDiligenciaHistorico(id: String, onSuccessListener: (processoHistorico: DiligenciaHistorico) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoHistorico = document.toObject(DiligenciaHistorico::class.java)
                    if (processoHistorico != null) {
                        onSuccessListener(processoHistorico)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun obterDiligenciasHistoricos(): List<DiligenciaHistorico>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(DiligenciaHistorico::class.java)
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterDiligenciaHistorico(id: String): DiligenciaHistorico? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val tipo = document.toObject(DiligenciaHistorico::class.java)!!
                    continuation.resume(tipo)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}

interface IDiligenciaHistoricoRepository {
    fun obterDiligenciasHistoricos(onSuccessListener: (lista: List<DiligenciaHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterDiligenciaHistorico(id: String, onSuccessListener: (processoHistorico: DiligenciaHistorico) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterDiligenciasHistoricos(): List<DiligenciaHistorico>?
    suspend fun obterDiligenciaHistorico(id: String): DiligenciaHistorico?
}