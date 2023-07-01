package com.example.advogo.repositories

import com.example.advogo.models.DiligenciaTipo
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DiligenciaTipoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IDiligenciaTipoRepository {
    override fun obterDiligenciasTipos(onSuccessListener: (List<DiligenciaTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TIPOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(DiligenciaTipo::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterDiligenciaTipo(id: String, onSuccessListener: (processoTipo: DiligenciaTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TIPOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoTipo = document.toObject(DiligenciaTipo::class.java)
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

    override suspend fun obterDiligenciasTipos(): List<DiligenciaTipo>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_TIPOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(DiligenciaTipo::class.java)
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterDiligenciaTipo(id: String): DiligenciaTipo? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_TIPOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val tipo = document.toObject(DiligenciaTipo::class.java)!!
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

interface IDiligenciaTipoRepository {
    fun obterDiligenciasTipos(onSuccessListener: (lista: List<DiligenciaTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterDiligenciaTipo(id: String, onSuccessListener: (processoTipo: DiligenciaTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterDiligenciasTipos(): List<DiligenciaTipo>?
    suspend fun obterDiligenciaTipo(id: String): DiligenciaTipo?
}