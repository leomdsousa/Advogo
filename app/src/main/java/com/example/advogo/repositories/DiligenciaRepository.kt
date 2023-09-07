package com.example.advogo.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.advogo.models.Diligencia
import com.example.advogo.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
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


class DiligenciaRepository @Inject constructor(
    context: Context,
    private val firebaseStore: FirebaseFirestore,
    private val processoRepository: Provider<ProcessoRepository>,
    private val advogadoRepository: Provider<AdvogadoRepository>,
    private val tipoDiligenciaRepository: DiligenciaTipoRepository,
    private val statusDiligenciaRepository: DiligenciaStatusRepository,
    private val diligenciaHistoricoRepository: IDiligenciaHistoricoRepository
) : IDiligenciaRepository {
    private val coroutineScope: CoroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: GlobalScope

    override fun obterDiligencias(onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val diligencias = document.toObjects(Diligencia::class.java)

                    coroutineScope.launch {
                        if (diligencias.isNotEmpty()) {
                            for (item in diligencias) {
                                val advogadoDeferred = async { advogadoRepository.get().obterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().obterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val historicoDeferred = async { diligenciaHistoricoRepository.obterDiligenciasHistoricoPorDiligencia(item.id!!) }
                                item.historicoLista = historicoDeferred.await()
                            }

                            onSuccessListener(diligencias)
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
    override fun obterDiligencia(id: String, onSuccessListener: (diligencia: Diligencia) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val diligencia = document.toObject(Diligencia::class.java)!!

                    coroutineScope.launch {
                        val advogadoDeferred = async { advogadoRepository.get().obterAdvogado(diligencia.advogado!!) }
                        val processoDeferred = async { processoRepository.get().obterProcesso(diligencia.processo!!) }
                        val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(diligencia.status!!) }
                        val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(diligencia.tipo!!) }

                        diligencia.advogadoObj = advogadoDeferred.await()
                        diligencia.processoObj = processoDeferred.await()
                        diligencia.statusObj = statusDeferred.await()
                        diligencia.tipoObj = tipoDeferred.await()

                        val historicoDeferred = async { diligenciaHistoricoRepository.obterDiligenciasHistoricoPorDiligencia(diligencia.id!!) }
                        diligencia.historicoLista = historicoDeferred.await()

                        onSuccessListener(diligencia)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterDiligenciasByDescricaoContains(text: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .orderBy(Constants.DILIGENCIAS_DESCRICAO)
            .whereGreaterThanOrEqualTo(Constants.DILIGENCIAS_DESCRICAO, text)
            .whereLessThanOrEqualTo(Constants.DILIGENCIAS_DESCRICAO, "${text}\uF7FF")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val diligencias = documents.toObjects(Diligencia::class.java)!!

                    coroutineScope.launch {
                        if (diligencias.isNotEmpty()) {
                            for (item in diligencias) {
                                val advogadoDeferred = async { advogadoRepository.get().obterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().obterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val historicoDeferred = async { diligenciaHistoricoRepository.obterDiligenciasHistoricoPorDiligencia(item.id!!) }
                                item.historicoLista = historicoDeferred.await()
                            }

                            onSuccessListener(diligencias)
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
    override fun obterDiligenciasPorProcesso(numeroProcesso: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .whereEqualTo(Constants.DILIGENCIAS_PROCESSO, numeroProcesso)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val diligencias = document.toObjects(Diligencia::class.java)!!

                    coroutineScope.launch {
                        if (diligencias.isNotEmpty()) {
                            for (item in diligencias) {
                                val advogadoDeferred = async { advogadoRepository.get().obterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().obterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val historicoDeferred = async { diligenciaHistoricoRepository.obterDiligenciasHistoricoPorDiligencia(item.id!!) }
                                item.historicoLista = historicoDeferred.await()
                            }

                            onSuccessListener(diligencias)
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
    override fun obterDiligenciasPorAdvogado(emailAdvogado: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .whereEqualTo(Constants.DILIGENCIAS_ADVOGADO, emailAdvogado)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val diligencias = document.toObjects(Diligencia::class.java)

                    coroutineScope.launch {
                        if (diligencias.isNotEmpty()) {
                            for (item in diligencias) {
                                val advogadoDeferred = async { advogadoRepository.get().obterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().obterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val historicoDeferred = async { diligenciaHistoricoRepository.obterDiligenciasHistoricoPorDiligencia(item.id!!) }
                                item.historicoLista = historicoDeferred.await()
                            }

                            onSuccessListener(diligencias)
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
    override fun adicionarDiligencia(model: Diligencia, onSuccessListener: (id: String) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener(it.id)
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarDiligencia(model: Diligencia, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarDiligencia(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override suspend fun obterDiligencia(id: String): Diligencia? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    coroutineScope.launch {
                        val resultado = document.toObject(Diligencia::class.java)!!

                        val advogadoDeferred = async { advogadoRepository.get().obterAdvogado(resultado.advogado!!) }
                        val processoDeferred = async { processoRepository.get().obterProcesso(resultado.processo!!) }
                        val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(resultado.status!!) }
                        val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(resultado.tipo!!) }

                        resultado.advogadoObj = advogadoDeferred.await()
                        resultado.processoObj = processoDeferred.await()
                        resultado.statusObj = statusDeferred.await()
                        resultado.tipoObj = tipoDeferred.await()

                        val historicoDeferred = async { diligenciaHistoricoRepository.obterDiligenciasHistoricoPorDiligencia(resultado.id!!) }
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
    override suspend fun obterDiligenciasPorData(data: String): List<Diligencia>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .whereGreaterThanOrEqualTo(Constants.DILIGENCIAS_DATA, data)
            .whereLessThanOrEqualTo(Constants.DILIGENCIAS_DATA, data)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    coroutineScope.launch {
                        val resultado = document.toObjects(Diligencia::class.java)

                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.get().obterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().obterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val historicoDeferred = async { diligenciaHistoricoRepository.obterDiligenciasHistoricoPorDiligencia(item.id!!) }
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
    override suspend fun obterDiligenciasPorData(dataInicio: String, dataFinal: String): List<Diligencia>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .whereGreaterThanOrEqualTo(Constants.DILIGENCIAS_DATA, dataInicio)
            .whereLessThanOrEqualTo(Constants.DILIGENCIAS_DATA, dataFinal)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    coroutineScope.launch {
                        val resultado = document.toObjects(Diligencia::class.java)

                        if (resultado.isNotEmpty()) {
                            for (item in resultado) {
                                val advogadoDeferred = async { advogadoRepository.get().obterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().obterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.obterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.obterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()

                                val historicoDeferred = async { diligenciaHistoricoRepository.obterDiligenciasHistoricoPorDiligencia(item.id!!) }
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
}

interface IDiligenciaRepository {
    fun obterDiligencias(onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterDiligencia(id: String, onSuccessListener: (diligencia: Diligencia) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterDiligenciasByDescricaoContains(text: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterDiligenciasPorProcesso(numeroProcesso: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterDiligenciasPorAdvogado(emailAdvogado: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun adicionarDiligencia(model: Diligencia, onSuccessListener: (id: String) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarDiligencia(model: Diligencia, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarDiligencia(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun obterDiligencia(id: String): Diligencia?
    suspend fun obterDiligenciasPorData(data: String): List<Diligencia>?
    suspend fun obterDiligenciasPorData(dataInicio: String, dataFinal: String): List<Diligencia>?


}

