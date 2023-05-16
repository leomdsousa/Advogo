package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.util.concurrent.CountDownLatch
import javax.inject.Inject
import javax.inject.Provider

class ProcessoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore,
    private val advogadoRepository: AdvogadoRepository,
    private val clienteRepository: ClienteRepository,
    private val diligenciaRepository: Provider<DiligenciaRepository>,
    private val tipoProcessoRepository: ProcessoTipoRepository,
    private val statusProcessoRepository: ProcessoStatusRepository
): IProcessoRepository {
    @OptIn(DelicateCoroutinesApi::class)
    override fun ObterProcessos(onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val processos = document.toObjects(Processo::class.java)

                    val job = GlobalScope.launch {
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
                            }
                        }
                    }

                    job.invokeOnCompletion { cause ->
                        if (cause != null) {
                            onFailureListener(null)
                        } else {
                            onSuccessListener(processos)
                        }
                    }

//                    for (item in processos) {
//                        GlobalScope.launch {
//                            clienteRepository.ObterCliente(
//                                item.cliente!!,
//                                { ret ->
//                                    item.clienteObj = ret
//                                },
//                                { null } //TODO("Implementar")
//                            )
//                        }
//
//                        GlobalScope.launch {
//                            advogadoRepository.ObterAdvogado(
//                                item.advogado!!,
//                                { ret ->
//                                    item.advogadoObj = ret
//                                },
//                                { null } //TODO("Implementar")
//                            )
//                        }
//
//                        GlobalScope.launch {
//                            statusProcessoRepository.ObterProcessoStatus(
//                                item.status!!,
//                                { ret ->
//                                    item.statusObj = ret
//                                },
//                                { null } //TODO("Implementar")
//                            )
//                        }
//
//                        GlobalScope.launch {
//                            tipoProcessoRepository.ObterProcessoTipo(
//                                item.tipo!!,
//                                { ret ->
//                                    item.tipoObj = ret
//                                },
//                                { null } //TODO("Implementar")
//                            )
//                        }
//                    }
//
//                    onSuccessListener(processos)
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

                    val countDownLatch = CountDownLatch(4)

                    GlobalScope.launch {
                        clienteRepository.ObterCliente(
                            processo.cliente!!,
                            { ret ->
                                processo.clienteObj = ret
                            },
                            { null } //TODO("Implementar")
                        )
                    }

                    advogadoRepository.ObterAdvogado(
                        processo.advogado!!,
                        { ret ->
                            processo.advogadoObj = ret
                            countDownLatch.countDown()
                        },
                        { null } //TODO("Implementar")
                    )

                    statusProcessoRepository.ObterProcessoStatus(
                        processo.status!!,
                        { ret ->
                            processo.statusObj = ret
                            countDownLatch.countDown()
                        },
                        { null } //TODO("Implementar")
                    )

                    tipoProcessoRepository.ObterProcessoTipo(
                        processo.tipo!!,
                        { ret ->
                            processo.tipoObj = ret
                            countDownLatch.countDown()
                        },
                        { null } //TODO("Implementar")
                    )

                    try {
                        countDownLatch.await()
                        onSuccessListener(processo)
                    } catch (ex: InterruptedException) {
                        onFailureListener(ex)
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
                    onSuccessListener(processo)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun AdicionarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(model.id!!)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener
            }
            .addOnFailureListener {
                onFailureListener
            }
    }

    override fun AtualizarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(model.id!!)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener
            }
            .addOnFailureListener {
                onFailureListener
            }
    }

    override fun DeletarProcesso(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener
            }
            .addOnFailureListener {
                onFailureListener
            }
    }
}

interface IProcessoRepository {
    fun ObterProcessos(onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcesso(id: String, onSuccessListener: (processo: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterProcessoPorNumero(numero: String, onSuccessListener: (processo: Processo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AdicionarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
    fun AtualizarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
    fun DeletarProcesso(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
}