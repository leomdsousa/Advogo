package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
    private val _diligenciaRepository = diligenciaRepository.get()

    override fun ObterProcessos(onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val processos = document.toObjects(Processo::class.java)

                    for (item in processos) {
                        clienteRepository.ObterCliente(
                            item.cliente!!,
                            { ret -> item.clienteObj = ret },
                            { null } //TODO("Implementar")
                        )

                        advogadoRepository.ObterAdvogado(
                            item.advogado!!,
                            { ret -> item.advogadoObj = ret },
                            { null } //TODO("Implementar")
                        )

                        statusProcessoRepository.ObterProcessoStatus(
                            item.status!!,
                            { ret -> item.statusObj = ret },
                            { null } //TODO("Implementar")
                        )

                        tipoProcessoRepository.ObterProcessoTipo(
                            item.tipo!!,
                            { ret -> item.tipoObj = ret },
                            { null } //TODO("Implementar")
                        )
                    }

                    onSuccessListener(processos)
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

                    clienteRepository.ObterCliente(
                        processo.cliente!!,
                        { ret -> processo.clienteObj = ret },
                        { null } //TODO("Implementar")
                    )

                    advogadoRepository.ObterAdvogado(
                        processo.advogado!!,
                        { ret -> processo.advogadoObj = ret },
                        { null } //TODO("Implementar")
                    )

                    statusProcessoRepository.ObterProcessoStatus(
                        processo.status!!,
                        { ret -> processo.statusObj = ret },
                        { null } //TODO("Implementar")
                    )

                    tipoProcessoRepository.ObterProcessoTipo(
                        processo.tipo!!,
                        { ret -> processo.tipoObj = ret },
                        { null } //TODO("Implementar")
                    )

                    onSuccessListener(processo)
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