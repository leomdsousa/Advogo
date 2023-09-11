package com.example.advogo.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.advogo.models.Processo
import com.example.advogo.utils.constants.Constants
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
    private val andamentoRepository: IProcessoAndamentoRepository,
    private val historicoRepository: IProcessoHistoricoRepository
): IProcessoRepository {
    private val coroutineScope: CoroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: GlobalScope

    override fun obterProcessos(onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val processos = document.toObjects(Processo::class.java)

                    coroutineScope.launch {
                        if (processos.isNotEmpty()) {
                            for (item in processos) {
                                val clienteDeferred = async { clienteRepository.obterCliente(item.cliente!!) }
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(item.tipo!!) }

                                item.clienteObj = clienteDeferred.await()
                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val anexosDeferred = async { anexoRepository.obterAnexosPorProcesso(item.numero!!) }
                                item.anexosLista = anexosDeferred.await()

                                val andamentosDeferred = async { andamentoRepository.obterAndamentosPorProcesso(item.numero!!) }
                                item.andamentosLista = andamentosDeferred.await()

                                val historicoDeferred = async { historicoRepository.obterProcessosHistoricosPorProcesso(item.numero!!) }
                                item.historicoLista = historicoDeferred.await()
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
    override fun obterProcesso(id: String, onSuccessListener: (process: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val processo = document.toObject(Processo::class.java)!!

                    coroutineScope.launch {
                        val clienteDeferred = async { clienteRepository.obterCliente(processo.cliente!!) }
                        val advogadoDeferred = async { advogadoRepository.obterAdvogado(processo.advogado!!) }
                        val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(processo.status!!) }
                        val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(processo.tipo!!) }

                        processo.clienteObj = clienteDeferred.await()
                        processo.advogadoObj = advogadoDeferred.await()
                        processo.statusObj = statusDeferred.await()
                        processo.tipoObj = tipoDeferred.await()

                        val anexosDeferred = async { anexoRepository.obterAnexosPorProcesso(processo.numero!!) }
                        processo.anexosLista = anexosDeferred.await()

                        val andamentosDeferred = async { andamentoRepository.obterAndamentosPorProcesso(processo.numero!!) }
                        processo.andamentosLista = andamentosDeferred.await()

                        val historicoDeferred = async { historicoRepository.obterProcessosHistoricosPorProcesso(processo.numero!!) }
                        processo.historicoLista = historicoDeferred.await()

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
    override fun obterProcessoPorNumero(numero: String, onSuccessListener: (processo: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .whereEqualTo(Constants.PROCESSOS_NUMERO, numero)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val processo = documents.first().toObject(Processo::class.java)!!

                    coroutineScope.launch {
                        val clienteDeferred = async { clienteRepository.obterCliente(processo.cliente!!) }
                        val advogadoDeferred = async { advogadoRepository.obterAdvogado(processo.advogado!!) }
                        val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(processo.status!!) }
                        val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(processo.tipo!!) }

                        processo.clienteObj = clienteDeferred.await()
                        processo.advogadoObj = advogadoDeferred.await()
                        processo.statusObj = statusDeferred.await()
                        processo.tipoObj = tipoDeferred.await()

                        val anexosDeferred = async { anexoRepository.obterAnexosPorProcesso(processo.numero!!) }
                        processo.anexosLista = anexosDeferred.await()

                        val andamentosDeferred = async { andamentoRepository.obterAndamentosPorProcesso(processo.numero!!) }
                        processo.andamentosLista = andamentosDeferred.await()

                        val historicoDeferred = async { historicoRepository.obterProcessosHistoricosPorProcesso(processo.numero!!) }
                        processo.historicoLista = historicoDeferred.await()

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
    override fun obterProcessosByTituloContains(text: String, onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .orderBy(Constants.PROCESSOS_TITULO)
            .whereGreaterThanOrEqualTo(Constants.PROCESSOS_TITULO, text)
            .whereLessThanOrEqualTo(Constants.PROCESSOS_TITULO, "${text}\uF7FF")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val processos = documents.toObjects(Processo::class.java)

                    coroutineScope.launch {
                        if (processos.isNotEmpty()) {
                            for (item in processos) {
                                val clienteDeferred = async { clienteRepository.obterCliente(item.cliente!!) }
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(item.tipo!!) }

                                item.clienteObj = clienteDeferred.await()
                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val anexosDeferred = async { anexoRepository.obterAnexosPorProcesso(item.numero!!) }
                                item.anexosLista = anexosDeferred.await()

                                val andamentosDeferred = async { andamentoRepository.obterAndamentosPorProcesso(item.numero!!) }
                                item.andamentosLista = andamentosDeferred.await()

                                val historicoDeferred = async { historicoRepository.obterProcessosHistoricosPorProcesso(item.numero!!) }
                                item.historicoLista = historicoDeferred.await()
                            }

                            onSuccessListener(processos)
                        }
                    }
                } else {
                    onSuccessListener(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun adicionarProcesso(model: Processo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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
    override fun atualizarProcesso(model: Processo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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
    override fun deletarProcesso(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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

    override suspend fun obterProcessos(): List<Processo>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val resultado = document.toObjects(Processo::class.java)

                    coroutineScope.launch {
                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val clienteDeferred = async { clienteRepository.obterCliente(item.cliente!!) }
                                val advogadoDeferred = async { advogadoRepository.obterAdvogado(item.advogado!!) }
                                val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(item.status!!) }
                                val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(item.tipo!!) }

                                item.clienteObj = clienteDeferred.await()
                                item.advogadoObj = advogadoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val anexosDeferred = async { anexoRepository.obterAnexosPorProcesso(item.numero!!) }
                                item.anexosLista = anexosDeferred.await()

                                val andamentosDeferred = async { andamentoRepository.obterAndamentosPorProcesso(item.numero!!) }
                                item.andamentosLista = andamentosDeferred.await()

                                val historicoDeferred = async { historicoRepository.obterProcessosHistoricosPorProcesso(item.numero!!) }
                                item.historicoLista = historicoDeferred.await()
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
    override suspend fun obterProcesso(id: String): Processo? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val resultado = document.toObject(Processo::class.java)!!

                    coroutineScope.launch {
                        val clienteDeferred = async { clienteRepository.obterCliente(resultado.cliente!!) }
                        val advogadoDeferred = async { advogadoRepository.obterAdvogado(resultado.advogado!!) }
                        val statusDeferred = async { statusProcessoRepository.obterProcessoStatus(resultado.status!!) }
                        val tipoDeferred = async { tipoProcessoRepository.obterProcessoTipo(resultado.tipo!!) }

                        resultado.clienteObj = clienteDeferred.await()
                        resultado.advogadoObj = advogadoDeferred.await()
                        resultado.statusObj = statusDeferred.await()
                        resultado.tipoObj = tipoDeferred.await()

                        val anexosDeferred = async { anexoRepository.obterAnexosPorProcesso(resultado.numero!!) }
                        resultado.anexosLista = anexosDeferred.await()

                        val andamentosDeferred = async { andamentoRepository.obterAndamentosPorProcesso(resultado.numero!!) }
                        resultado.andamentosLista = andamentosDeferred.await()

                        val historicoDeferred = async { historicoRepository.obterProcessosHistoricosPorProcesso(resultado.numero!!) }
                        resultado.historicoLista = historicoDeferred.await()

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
    fun obterProcessos(onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcesso(id: String, onSuccessListener: (processo: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessosByTituloContains(text: String, onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterProcessoPorNumero(numero: String, onSuccessListener: (processo: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun adicionarProcesso(model: Processo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarProcesso(model: Processo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarProcesso(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterProcessos(): List<Processo>?
    suspend fun obterProcesso(id: String): Processo?
}