package com.example.advogo.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.advogo.models.ProcessoAndamento
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoAndamentoRepository @Inject constructor(
    context: Context,
    private val firebaseStore: FirebaseFirestore,
    private val advogadoRepository: AdvogadoRepository,
    private val processoTipoAndamentoRepository: ProcessoTipoAndamentoRepository,
    private val processoStatusAndamentoRepository: ProcessoStatusAndamentoRepository
): IProcessoAndamentoRepository {
    private val coroutineScope: CoroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: GlobalScope

    override fun obterProcessosAndamentos(onSuccessListener: (List<ProcessoAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(ProcessoAndamento::class.java)

                    coroutineScope.launch {
                        if (lista.isNotEmpty()) {
                            for (item in lista) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { processoStatusAndamentoRepository.obterProcessoStatusAndamento(item.status!!) }
                                val tipoDeferred = async { processoTipoAndamentoRepository.obterProcessoTipoAndamento(item.tipo!!) }

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
    override fun obterProcessoAndamento(id: String, onSuccessListener: (ProcessoAndamento: ProcessoAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val andamento = document.toObject(ProcessoAndamento::class.java)

                    if (andamento != null) {

                        coroutineScope.launch {
                            val advogadoDeferred = async { advogadoRepository.obterAdvogado(andamento.advogado!!) }
                            val statusDeferred = async { processoStatusAndamentoRepository.obterProcessoStatusAndamento(andamento.status!!) }
                            val tipoDeferred = async { processoTipoAndamentoRepository.obterProcessoTipoAndamento(andamento.tipo!!) }

                            andamento.advogadoObj = advogadoDeferred.await()
                            andamento.statusObj = statusDeferred.await()
                            andamento.tipoObj = tipoDeferred.await()

                            onSuccessListener(andamento)
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

    override suspend fun obterProcessosAndamentos(): List<ProcessoAndamento>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(ProcessoAndamento::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { processoStatusAndamentoRepository.obterProcessoStatusAndamento(item.status!!) }
                                val tipoDeferred = async { processoTipoAndamentoRepository.obterProcessoTipoAndamento(item.tipo!!) }

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
    override suspend fun obterProcessoAndamento(id: String): ProcessoAndamento? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(ProcessoAndamento::class.java)!!

                    coroutineScope.launch {
                        val advogadoDeferred = async { advogadoRepository.obterAdvogado(resultado.advogado!!) }
                        val statusDeferred = async { processoStatusAndamentoRepository.obterProcessoStatusAndamento(resultado.status!!) }
                        val tipoDeferred = async { processoTipoAndamentoRepository.obterProcessoTipoAndamento(resultado.tipo!!) }

                        resultado.advogadoObj = advogadoDeferred.await()
                        resultado.statusObj = statusDeferred.await()
                        resultado.tipoObj = tipoDeferred.await()

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

    override suspend fun obterAndamentosPorLista(ids: List<String>): List<ProcessoAndamento>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(ProcessoAndamento::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { processoStatusAndamentoRepository.obterProcessoStatusAndamento(item.status!!) }
                                val tipoDeferred = async { processoTipoAndamentoRepository.obterProcessoTipoAndamento(item.tipo!!) }

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

    override fun adicionarProcessoAndamento(model: ProcessoAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarProcessoAndamento(model: ProcessoAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarProcessoAndamento(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_ANDAMENTOS_TABLE)
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

interface IProcessoAndamentoRepository {
    fun obterProcessosAndamentos(onSuccessListener: (lista: List<ProcessoAndamento>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessoAndamento(id: String, onSuccessListener: (ProcessoAndamento: ProcessoAndamento) -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessosAndamentos(): List<ProcessoAndamento>?
    suspend fun obterProcessoAndamento(id: String): ProcessoAndamento?
    suspend fun obterAndamentosPorLista(ids: List<String>): List<ProcessoAndamento>?

    fun adicionarProcessoAndamento(model: ProcessoAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarProcessoAndamento(model: ProcessoAndamento, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarProcessoAndamento(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}