package com.example.advogo.repositories

import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProcessoRepository @Inject constructor(): IProcessoRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterProcessos(onSuccessListener: OnSuccessListener<List<Processo>>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .get()
    }

    override fun ObterProcesso(id: String, onSuccessListener: OnSuccessListener<Processo>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .whereEqualTo(Constants.PROCESSOS_ID, id)
            .get()
    }

    override fun ObterProcessoPorNumero(numero: String, onSuccessListener: OnSuccessListener<Processo>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.PROCESSOS_TABLE)
            .whereEqualTo(Constants.PROCESSOS_NUMERO, numero)
            .get()
    }

    override fun AdicionarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }

    override fun AtualizarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }

    override fun DeletarProcesso(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }
}

interface IProcessoRepository {
    fun ObterProcessos(onSuccessListener: OnSuccessListener<List<Processo>>, onFailureListener: OnFailureListener)
    fun ObterProcesso(id: String, onSuccessListener: OnSuccessListener<Processo>, onFailureListener: OnFailureListener)
    fun ObterProcessoPorNumero(numero: String, onSuccessListener: OnSuccessListener<Processo>, onFailureListener: OnFailureListener)
    fun AdicionarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
    fun AtualizarProcesso(model: Processo, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
    fun DeletarProcesso(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
}