package com.example.advogo.repositories

import com.example.advogo.models.ProcessoTipo
import com.example.advogo.utils.constants.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoTipoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IProcessoTipoRepository {
    override fun obterProcessosTipos(onSuccessListener: (List<ProcessoTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoTipo::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterProcessoTipo(id: String, onSuccessListener: (processoTipo: ProcessoTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoTipo = document.toObject(ProcessoTipo::class.java)
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

    override suspend fun obterProcessosTipos(): List<ProcessoTipo>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(ProcessoTipo::class.java)
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterProcessoTipo(id: String): ProcessoTipo? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(ProcessoTipo::class.java)!!
                    continuation.resume(resultado)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override fun adicionarProcessoTipo(model: ProcessoTipo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarProcessoTipo(model: ProcessoTipo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarProcessoTipo(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TIPOS_TABLE)
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

interface IProcessoTipoRepository {
    fun obterProcessosTipos(onSuccessListener: (lista: List<ProcessoTipo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessoTipo(id: String, onSuccessListener: (processoTipo: ProcessoTipo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessosTipos(): List<ProcessoTipo>?
    suspend fun obterProcessoTipo(id: String): ProcessoTipo?

    fun adicionarProcessoTipo(model: ProcessoTipo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarProcessoTipo(model: ProcessoTipo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarProcessoTipo(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}