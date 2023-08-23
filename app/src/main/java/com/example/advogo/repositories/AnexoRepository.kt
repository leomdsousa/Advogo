package com.example.advogo.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.advogo.models.Anexo
import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class AnexoRepository @Inject constructor(
    context: Context,
    private val firebaseStore: FirebaseFirestore,
    private val advogadoRepository: AdvogadoRepository,
) : IAnexoRepository {
    private val coroutineScope: CoroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: GlobalScope

    override fun obterAnexos(onSuccessListener: (lista: List<Anexo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .orderBy(Constants.ANEXOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val anexos = document.toObjects(Anexo::class.java)
                    onSuccessListener(anexos)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterAnexosPorProcesso(numeroProcesso: String, onSuccessListener: (lista: List<Anexo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .whereEqualTo(Constants.ANEXOS_PROCESSO, numeroProcesso)
            .orderBy(Constants.ANEXOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val anexos = document.toObjects(Anexo::class.java)
                    onSuccessListener(anexos)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun obterAnexo(id: String, onSuccessListener: (Anexo: Anexo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val anexo = document.toObject(Anexo::class.java)!!
                    onSuccessListener(anexo)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun adicionarAnexo(model: Anexo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarAnexo(model: Anexo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarAnexo(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override suspend fun obterAnexosPorLista(ids: List<String>): List<Anexo>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .whereIn(FieldPath.documentId(), ids)
            .orderBy(Constants.ANEXOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(Anexo::class.java)

                    coroutineScope.launch {
                        if(resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }

                                item.advogadoObj = advogadoDeferred.await()
                            }

                            continuation.resume(resultado)
                        }
                    }
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterAnexosPorProcesso(numeroProcesso: String): List<Anexo>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .whereEqualTo(Constants.ANEXOS_PROCESSO, numeroProcesso)
            .orderBy(Constants.ANEXOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(Anexo::class.java)

                    coroutineScope.launch {
                        if(resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                item.advogadoObj = advogadoDeferred.await()
                            }

                            continuation.resume(resultado)
                        }
                    }
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
}

interface IAnexoRepository {
    fun obterAnexos(onSuccessListener: (lista: List<Anexo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterAnexosPorProcesso(numeroProcesso: String, onSuccessListener: (lista: List<Anexo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterAnexo(id: String, onSuccessListener: (Anexo: Anexo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun adicionarAnexo(model: Anexo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarAnexo(model: Anexo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarAnexo(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterAnexosPorLista(ids: List<String>): List<Anexo>?
    suspend fun obterAnexosPorProcesso(numeroProcesso: String): List<Anexo>?
}