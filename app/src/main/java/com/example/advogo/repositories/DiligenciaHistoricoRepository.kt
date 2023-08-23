package com.example.advogo.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.advogo.models.DiligenciaHistorico
import com.example.advogo.utils.Constants
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

class DiligenciaHistoricoRepository @Inject constructor(
    context: Context,
    private val firebaseStore: FirebaseFirestore,
    private val advogadoRepository: AdvogadoRepository,
    private val tipoDiligenciaRepository: DiligenciaTipoRepository,
    private val statusDiligenciaRepository: DiligenciaStatusRepository,
): IDiligenciaHistoricoRepository {
    private val coroutineScope: CoroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: GlobalScope

    override fun obterDiligenciasHistoricos(onSuccessListener: (List<DiligenciaHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .orderBy(Constants.DILIGENCIAS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
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
    override fun obterDiligenciasHistoricosPorDiligencia(id:String, onSuccessListener: (List<DiligenciaHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .whereEqualTo(Constants.DILIGENCIAS_HISTORICOS_DILIGENCIA, id)
            .orderBy(Constants.DILIGENCIAS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
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
    override fun adicionarDiligenciaHistorico(model: DiligenciaHistorico, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarDiligenciaHistorico(model: DiligenciaHistorico, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarDiligenciaHistorico(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override suspend fun obterDiligenciasHistoricoPorLista(ids: List<String>): List<DiligenciaHistorico>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .whereIn(FieldPath.documentId(), ids)
            .orderBy(Constants.DILIGENCIAS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(DiligenciaHistorico::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()
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
    override suspend fun obterDiligenciasHistoricoPorDiligencia(id: String): List<DiligenciaHistorico>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .whereEqualTo(Constants.DILIGENCIAS_HISTORICOS_DILIGENCIA, id)
            .orderBy(Constants.DILIGENCIAS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(DiligenciaHistorico::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()
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
    override suspend fun obterDiligenciasHistoricos(): List<DiligenciaHistorico>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .orderBy(Constants.DILIGENCIAS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(DiligenciaHistorico::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

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
    override suspend fun obterDiligenciaHistorico(id: String): DiligenciaHistorico? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_HISTORICOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(DiligenciaHistorico::class.java)!!

                    coroutineScope.launch {
                        if (resultado != null) {
                            val advogadoDeferred = async { advogadoRepository.obterAdvogado(resultado.advogado!!) }
                            val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(resultado.status!!) }
                            val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(resultado.tipo!!) }

                            resultado.advogadoObj = advogadoDeferred.await()
                            resultado.statusObj = statusDeferred.await()
                            resultado.tipoObj = tipoDeferred.await()
                        }

                        continuation.resume(resultado)
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

interface IDiligenciaHistoricoRepository {
    fun obterDiligenciasHistoricos(onSuccessListener: (lista: List<DiligenciaHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterDiligenciasHistoricosPorDiligencia(id:String, onSuccessListener: (List<DiligenciaHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterDiligenciaHistorico(id: String, onSuccessListener: (processoHistorico: DiligenciaHistorico) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun adicionarDiligenciaHistorico(model: DiligenciaHistorico, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarDiligenciaHistorico(model: DiligenciaHistorico, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarDiligenciaHistorico(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterDiligenciasHistoricoPorLista(ids: List<String>): List<DiligenciaHistorico>?
    suspend fun obterDiligenciasHistoricoPorDiligencia(id: String): List<DiligenciaHistorico>?
    suspend fun obterDiligenciasHistoricos(): List<DiligenciaHistorico>?
    suspend fun obterDiligenciaHistorico(id: String): DiligenciaHistorico?
}