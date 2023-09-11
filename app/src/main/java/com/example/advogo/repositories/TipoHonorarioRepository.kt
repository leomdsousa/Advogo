package com.example.advogo.repositories

import com.example.advogo.models.TiposHonorarios
import com.example.advogo.utils.constants.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TipoHonorarioRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): ITipoHonorarioRepository {
    override fun obterProcessosHonorarios(onSuccessListener: (List<TiposHonorarios>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_HONORARIOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(TiposHonorarios::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterTipoParte(id: String, onSuccessListener: (TiposHonorarios: TiposHonorarios) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_HONORARIOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val TiposHonorarios = document.toObject(TiposHonorarios::class.java)
                    if (TiposHonorarios != null) {
                        onSuccessListener(TiposHonorarios)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun obterProcessosHonorarios(): List<TiposHonorarios>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.TIPOS_HONORARIOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(TiposHonorarios::class.java)
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterTipoParte(id: String): TiposHonorarios? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.TIPOS_HONORARIOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(TiposHonorarios::class.java)!!
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override fun adicionarTipoParte(model: TiposHonorarios, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_HONORARIOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarTipoParte(model: TiposHonorarios, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_HONORARIOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarTipoParte(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_HONORARIOS_TABLE)
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

interface ITipoHonorarioRepository {
    fun obterProcessosHonorarios(onSuccessListener: (lista: List<TiposHonorarios>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterTipoParte(id: String, onSuccessListener: (TiposHonorarios: TiposHonorarios) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessosHonorarios(): List<TiposHonorarios>?
    suspend fun obterTipoParte(id: String): TiposHonorarios?

    fun adicionarTipoParte(model: TiposHonorarios, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarTipoParte(model: TiposHonorarios, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarTipoParte(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}