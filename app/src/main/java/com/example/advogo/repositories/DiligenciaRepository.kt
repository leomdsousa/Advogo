package com.example.advogo.repositories

import com.example.advogo.models.Diligencia
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class DiligenciaRepository @Inject constructor(): IDiligenciaRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterDiligencias(onSuccessListener: OnSuccessListener<List<Diligencia>>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .get()
    }

    override fun ObterDiligencia(id: String, onSuccessListener: OnSuccessListener<Diligencia>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .whereEqualTo(Constants.DILIGENCIAS_ID, id)
            .get()
    }

    override fun ObterDiligenciasPorProcesso(numeroProcesso: String, onSuccessListener: OnSuccessListener<List<Diligencia>>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .whereEqualTo(Constants.DILIGENCIAS_PROCESSO, numeroProcesso)
            .get()
    }

    override fun ObterDiligenciasPorAdvogado(emailAdvogado: String, onSuccessListener: OnSuccessListener<List<Diligencia>>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .whereEqualTo(Constants.DILIGENCIAS_ADVOGADO, emailAdvogado)
            .get()
    }

    override fun AdicionarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }

    override fun AtualizarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }

    override fun DeletarDiligencia(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }
}

interface IDiligenciaRepository {
    fun ObterDiligencias(onSuccessListener: OnSuccessListener<List<Diligencia>>, onFailureListener: OnFailureListener)
    fun ObterDiligencia(id: String, onSuccessListener: OnSuccessListener<Diligencia>, onFailureListener: OnFailureListener)
    fun ObterDiligenciasPorProcesso(numeroProcesso: String, onSuccessListener: OnSuccessListener<List<Diligencia>>, onFailureListener: OnFailureListener)
    fun ObterDiligenciasPorAdvogado(emailAdvogado: String, onSuccessListener: OnSuccessListener<List<Diligencia>>, onFailureListener: OnFailureListener)
    fun AdicionarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
    fun AtualizarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
    fun DeletarDiligencia(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
}