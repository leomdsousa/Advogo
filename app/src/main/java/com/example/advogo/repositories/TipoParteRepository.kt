package com.example.advogo.repositories

import com.example.advogo.models.TiposPartes
import com.example.advogo.utils.constants.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TipoParteRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): ITipoParteRepository {
    override fun obterProcessosTipos(onSuccessListener: (List<TiposPartes>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_PARTES_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(TiposPartes::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterTipoParte(id: String, onSuccessListener: (TiposPartes: TiposPartes) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_PARTES_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val TiposPartes = document.toObject(TiposPartes::class.java)
                    if (TiposPartes != null) {
                        onSuccessListener(TiposPartes)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun obterProcessosTipos(): List<TiposPartes>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.TIPOS_PARTES_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(TiposPartes::class.java)
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterTipoParte(id: String): TiposPartes? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.TIPOS_PARTES_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(TiposPartes::class.java)!!
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override fun adicionarTipoParte(model: TiposPartes, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_PARTES_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarTipoParte(model: TiposPartes, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TIPOS_PARTES_TABLE)
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
            .collection(Constants.TIPOS_PARTES_TABLE)
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

interface ITipoParteRepository {
    fun obterProcessosTipos(onSuccessListener: (lista: List<TiposPartes>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterTipoParte(id: String, onSuccessListener: (TiposPartes: TiposPartes) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessosTipos(): List<TiposPartes>?
    suspend fun obterTipoParte(id: String): TiposPartes?

    fun adicionarTipoParte(model: TiposPartes, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarTipoParte(model: TiposPartes, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarTipoParte(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}