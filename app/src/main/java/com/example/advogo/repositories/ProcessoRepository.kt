package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProcessoRepository @Inject constructor(): IProcessoRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterProcessos(onSuccessListener: (lista: List<Processo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val processos = document.toObjects(Processo::class.java)
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
        TODO("Not yet implemented")
    }

    override fun AtualizarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun DeletarProcesso(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
        TODO("Not yet implemented")
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