package com.example.advogo.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.advogo.models.DiligenciaTipo
import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ProcessoRepository @Inject constructor(
    context: Context,
    private val firebaseStore: FirebaseFirestore,
    private val advogadoRepository: AdvogadoRepository,
    private val clienteRepository: ClienteRepository,
    private val diligenciaRepository: Provider<DiligenciaRepository>,
    private val tipoProcessoRepository: ProcessoTipoRepository,
    private val statusProcessoRepository: ProcessoStatusRepository,
    private val anexoRepository: AnexoRepository,
    private val andamentoRepository: IProcessoAndamentoRepository
): IProcessoRepository {
    private val coroutineScope: CoroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: GlobalScope

    override fun ObterProcessos(onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val processos = document.toObjects(Processo::class.java)

                    coroutineScope.launch {
                        if (processos.isNotEmpty()) {
                            for (item in processos) {
                                val clienteDeferred = async { clienteRepository.ObterCliente(item.cliente!!) }
                                val advogadoDeferred = async { advogadoRepository.ObterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.ObterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.ObterProcessoTipo(item.tipo!!) }

                                item.clienteObj = clienteDeferred.await()
                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                if(item.anexos?.isNotEmpty() == true) {
                                    val anexosDeferred = async { anexoRepository.ObterAnexosPorLista(item.anexos!!) }
                                    item.anexosLista = anexosDeferred.await()
                                } else {
                                    item.anexosLista = emptyList()
                                }

                                if(item.andamentos?.isNotEmpty() == true) {
                                    val andamentosDeferred = async { andamentoRepository.ObterAndamentosPorLista(item.andamentos!!) }
                                    item.andamentosLista = andamentosDeferred.await()
                                } else {
                                    item.andamentosLista = emptyList()
                                }
                            }

                            onSuccessListener(processos)
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
    override fun ObterProcesso(id: String, onSuccessListener: (process: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val processo = document.toObject(Processo::class.java)!!

                    coroutineScope.launch {
                        val clienteDeferred = async { clienteRepository.ObterCliente(processo.cliente!!) }
                        val advogadoDeferred = async { advogadoRepository.ObterAdvogado(processo.advogado!!) }
                        val statusDeferred = async { statusProcessoRepository.ObterProcessoStatus(processo.status!!) }
                        val tipoDeferred = async { tipoProcessoRepository.ObterProcessoTipo(processo.tipo!!) }

                        processo.clienteObj = clienteDeferred.await()
                        processo.advogadoObj = advogadoDeferred.await()
                        processo.statusObj = statusDeferred.await()
                        processo.tipoObj = tipoDeferred.await()

                        if(processo.anexos?.isNotEmpty() == true) {
                            val anexosDeferred = async { anexoRepository.ObterAnexosPorLista(processo.anexos!!) }
                            processo.anexosLista = anexosDeferred.await()
                        } else {
                            processo.anexosLista = emptyList()
                        }

                        if(processo.andamentos?.isNotEmpty() == true) {
                            val andamentosDeferred = async { andamentoRepository.ObterAndamentosPorLista(processo.andamentos!!) }
                            processo.andamentosLista = andamentosDeferred.await()
                        } else {
                            processo.andamentosLista = emptyList()
                        }

                        onSuccessListener(processo)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun ObterProcessoPorNumero(numero: String, onSuccessListener: (processo: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .whereEqualTo(Constants.PROCESSOS_NUMERO, numero)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val processo = documents.first().toObject(Processo::class.java)!!

                    coroutineScope.launch {
                        val clienteDeferred = async { clienteRepository.ObterCliente(processo.cliente!!) }
                        val advogadoDeferred = async { advogadoRepository.ObterAdvogado(processo.advogado!!) }
                        val statusDeferred = async { statusProcessoRepository.ObterProcessoStatus(processo.status!!) }
                        val tipoDeferred = async { tipoProcessoRepository.ObterProcessoTipo(processo.tipo!!) }

                        processo.clienteObj = clienteDeferred.await()
                        processo.advogadoObj = advogadoDeferred.await()
                        processo.statusObj = statusDeferred.await()
                        processo.tipoObj = tipoDeferred.await()

                        if(processo.anexos?.isNotEmpty() == true) {
                            val anexosDeferred = async { anexoRepository.ObterAnexosPorLista(processo.anexos!!) }
                            processo.anexosLista = anexosDeferred.await()
                        } else {
                            processo.anexosLista = emptyList()
                        }

                        if(processo.andamentos?.isNotEmpty() == true) {
                            val andamentosDeferred = async { andamentoRepository.ObterAndamentosPorLista(processo.andamentos!!) }
                            processo.andamentosLista = andamentosDeferred.await()
                        } else {
                            processo.andamentosLista = emptyList()
                        }

                        onSuccessListener(processo)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun AdicionarProcesso(model: Processo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun AtualizarProcesso(model: Processo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun DeletarProcesso(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override suspend fun ObterProcessos(): List<Processo>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(Processo::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val clienteDeferred = async { clienteRepository.ObterCliente(item.cliente!!) }
                                val advogadoDeferred = async { advogadoRepository.ObterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.ObterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.ObterProcessoTipo(item.tipo!!) }

                                item.clienteObj = clienteDeferred.await()
                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                if(item.anexos?.isNotEmpty() == true) {
                                    val anexosDeferred = async { anexoRepository.ObterAnexosPorLista(item.anexos!!) }
                                    item.anexosLista = anexosDeferred.await()
                                } else {
                                    item.anexosLista = emptyList()
                                }

                                if(item.andamentos?.isNotEmpty() == true) {
                                    val andamentosDeferred = async { andamentoRepository.ObterAndamentosPorLista(item.andamentos!!) }
                                    item.andamentosLista = andamentosDeferred.await()
                                } else {
                                    item.andamentosLista = emptyList()
                                }
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
    override suspend fun ObterProcesso(id: String): Processo? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(Processo::class.java)!!

                    coroutineScope.launch {
                        val clienteDeferred = async { clienteRepository.ObterCliente(resultado.cliente!!) }
                        val advogadoDeferred = async { advogadoRepository.ObterAdvogado(resultado.advogado!!) }
                        val statusDeferred = async { statusProcessoRepository.ObterProcessoStatus(resultado.status!!) }
                        val tipoDeferred = async { tipoProcessoRepository.ObterProcessoTipo(resultado.tipo!!) }

                        resultado.clienteObj = clienteDeferred.await()
                        resultado.advogadoObj = advogadoDeferred.await()
                        resultado.statusObj = statusDeferred.await()
                        resultado.tipoObj = tipoDeferred.await()

                        if(resultado.anexos?.isNotEmpty() == true) {
                            val anexosDeferred = async { anexoRepository.ObterAnexosPorLista(resultado.anexos!!) }
                            resultado.anexosLista = anexosDeferred.await()
                        } else {
                            resultado.anexosLista = emptyList()
                        }

                        if(resultado.andamentos?.isNotEmpty() == true) {
                            val andamentosDeferred = async { andamentoRepository.ObterAndamentosPorLista(resultado.andamentos!!) }
                            resultado.andamentosLista = andamentosDeferred.await()
                        } else {
                            resultado.andamentosLista = emptyList()
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

interface IProcessoRepository {
    fun ObterProcessos(onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcesso(id: String, onSuccessListener: (processo: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcessoPorNumero(numero: String, onSuccessListener: (processo: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AdicionarProcesso(model: Processo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AtualizarProcesso(model: Processo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun DeletarProcesso(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun ObterProcessos(): List<Processo>?
    suspend fun ObterProcesso(id: String): Processo?
}