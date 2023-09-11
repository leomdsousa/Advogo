package com.example.advogo.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.advogo.models.ProcessoHistorico
import com.example.advogo.utils.constants.Constants
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoHistoricoRepository @Inject constructor(
    context: Context,
    private val firebaseStore: FirebaseFirestore,
    private val advogadoRepository: AdvogadoRepository,
    private val tipoProcessoRepository: ProcessoTipoRepository,
    private val statusProcessoRepository: ProcessoStatusRepository,
): IProcessoHistoricoRepository {
    private val coroutineScope: CoroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: GlobalScope

    override fun obterProcessosHistoricos(onSuccessListener: (List<ProcessoHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .orderBy(Constants.PROCESSOS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoHistorico::class.java)

                    coroutineScope.launch {
                        if (lista.isNotEmpty()) {
                            for (item in lista) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()
                            }

                            onSuccessListener(lista)
                        }
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterProcessosHistoricosPorProcesso(numeroProcesso: String, onSuccessListener: (List<ProcessoHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .whereEqualTo(Constants.PROCESSOS_HISTORICOS_PROCESSO, numeroProcesso)
            .orderBy(Constants.PROCESSOS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoHistorico::class.java)

                    coroutineScope.launch {
                        if (lista.isNotEmpty()) {
                            for (item in lista) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()
                            }

                            onSuccessListener(lista)
                        }
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterProcessoHistorico(id: String, onSuccessListener: (processoHistorico: ProcessoHistorico) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val processoHistorico = document.toObject(ProcessoHistorico::class.java)

                    if (processoHistorico != null) {
                        coroutineScope.launch {
                            val advogadoDeferred = async { advogadoRepository.obterAdvogado(processoHistorico.advogado!!) }
                            val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(processoHistorico.status!!) }
                            val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(processoHistorico.tipo!!) }

                            processoHistorico.advogadoObj = advogadoDeferred.await()
                            processoHistorico.statusObj = statusDeferred.await()
                            processoHistorico.tipoObj = tipoDeferred.await()

                            onSuccessListener(processoHistorico)
                        }
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun adicionarProcessoHistorico(model: ProcessoHistorico, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarProcessoHistorico(model: ProcessoHistorico, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarProcessoHistorico(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override suspend fun obterProcessosHistoricoPorLista(ids: List<String>): List<ProcessoHistorico>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .whereIn(FieldPath.documentId(), ids)
            .orderBy(Constants.PROCESSOS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(ProcessoHistorico::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(item.tipo!!) }

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
    override suspend fun obterProcessosHistoricosPorProcesso(numeroProcesso: String): List<ProcessoHistorico>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .whereEqualTo(Constants.PROCESSOS_HISTORICOS_PROCESSO, numeroProcesso)
            .orderBy(Constants.PROCESSOS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(ProcessoHistorico::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(item.tipo!!) }

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
    override suspend fun obterProcessosHistoricos(): List<ProcessoHistorico>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .orderBy(Constants.PROCESSOS_HISTORICOS_DATA_TIMESTAMP, Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(ProcessoHistorico::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(item.tipo!!) }

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
    override suspend fun obterProcessoHistorico(id: String): ProcessoHistorico? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_HISTORICOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(ProcessoHistorico::class.java)!!

                    coroutineScope.launch {
                        if (resultado != null) {
                            val advogadoDeferred = async { advogadoRepository.obterAdvogado(resultado.advogado!!) }
                            val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(resultado.status!!) }
                            val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(resultado.tipo!!) }

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

interface IProcessoHistoricoRepository {
    fun obterProcessosHistoricos(onSuccessListener: (lista: List<ProcessoHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessosHistoricosPorProcesso(numeroProcesso: String, onSuccessListener: (List<ProcessoHistorico>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessoHistorico(id: String, onSuccessListener: (processoHistorico: ProcessoHistorico) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun adicionarProcessoHistorico(model: ProcessoHistorico, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarProcessoHistorico(model: ProcessoHistorico, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarProcessoHistorico(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessosHistoricoPorLista(ids: List<String>): List<ProcessoHistorico>?
    suspend fun obterProcessosHistoricosPorProcesso(numeroProcesso: String): List<ProcessoHistorico>?
    suspend fun obterProcessosHistoricos(): List<ProcessoHistorico>?
    suspend fun obterProcessoHistorico(id: String): ProcessoHistorico?
}