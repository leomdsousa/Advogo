package com.example.advogo.repositories

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.advogo.models.Diligencia
import com.example.advogo.models.DiligenciaStatus
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnSuccessListener
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
    private val statusDiligenciaRepository: DiligenciaStatusRepository
) : IDiligenciaRepository {
    private val coroutineScope: CoroutineScope = (context as? LifecycleOwner)?.lifecycleScope ?: GlobalScope

    override fun ObterDiligencias(onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val diligencias = document.toObjects(Diligencia::class.java)

                    coroutineScope.launch {
                        if (diligencias.isNotEmpty()) {
                            for (item in diligencias) {
                                val advogadoDeferred = async { advogadoRepository.get().ObterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().ObterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.ObterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.ObterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()
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
    override fun ObterDiligencia(id: String, onSuccessListener: (diligencia: Diligencia) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val diligencia = document.toObject(Diligencia::class.java)!!

                    coroutineScope.launch {
                        val advogadoDeferred = async { advogadoRepository.get().ObterAdvogado(diligencia.advogado!!) }
                        val processoDeferred = async { processoRepository.get().ObterProcesso(diligencia.processo!!) }
                        val statusDeferred = async { statusDiligenciaRepository.ObterDiligenciaStatus(diligencia.status!!) }
                        val tipoDeferred = async { tipoDiligenciaRepository.ObterDiligenciaTipo(diligencia.tipo!!) }

                        diligencia.advogadoObj = advogadoDeferred.await()
                        diligencia.processoObj = processoDeferred.await()
                        diligencia.statusObj = statusDeferred.await()
                        diligencia.tipoObj = tipoDeferred.await()

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
    override fun ObterDiligenciasPorProcesso(numeroProcesso: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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
                                val advogadoDeferred = async { advogadoRepository.get().ObterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().ObterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.ObterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.ObterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()
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
    override fun ObterDiligenciasPorAdvogado(emailAdvogado: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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
                                val advogadoDeferred = async { advogadoRepository.get().ObterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().ObterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.ObterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.ObterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
                                item.statusObj = statusDeferred.await()
                                item.tipoObj = tipoDeferred.await()
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
    override fun AdicionarDiligencia(model: Diligencia, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun AtualizarDiligencia(model: Diligencia, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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
    override fun DeletarDiligencia(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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

    override suspend fun ObterDiligenciasPorData(data: String): List<Diligencia>? = suspendCoroutine { continuation ->
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
                                val advogadoDeferred = async { advogadoRepository.get().ObterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().ObterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.ObterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.ObterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
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
    override suspend fun ObterDiligenciasPorData(dataInicio: String, dataFinal: String): List<Diligencia>? = suspendCoroutine { continuation ->
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
                                val advogadoDeferred = async { advogadoRepository.get().ObterAdvogado(item.advogado!!) }
                                val processoDeferred = async { processoRepository.get().ObterProcesso(item.processo!!) }
                                val statusDeferred = async { statusDiligenciaRepository.ObterDiligenciaStatus(item.status!!) }
                                val tipoDeferred = async { tipoDiligenciaRepository.ObterDiligenciaTipo(item.tipo!!) }

                                item.advogadoObj = advogadoDeferred.await()
                                item.processoObj = processoDeferred.await()
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

}

interface IDiligenciaRepository {
    fun ObterDiligencias(onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligencia(id: String, onSuccessListener: (diligencia: Diligencia) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligenciasPorProcesso(numeroProcesso: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligenciasPorAdvogado(emailAdvogado: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AdicionarDiligencia(model: Diligencia, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AtualizarDiligencia(model: Diligencia, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun DeletarDiligencia(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)

    suspend fun ObterDiligenciasPorData(data: String): List<Diligencia>?
    suspend fun ObterDiligenciasPorData(dataInicio: String, dataFinal: String): List<Diligencia>?


}

