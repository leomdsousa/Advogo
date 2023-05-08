package com.example.advogo.repositories

import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class DiligenciaRepository @Inject constructor(): IDiligenciaRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterDiligencias(onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.DILIGENCIAS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val diligencias = document.toObjects(Diligencia::class.java)
                    onSuccessListener(diligencias)
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
                    onSuccessListener(diligencia)
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
                    onSuccessListener(diligencias)
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
                    val diligencias = document.toObjects(Diligencia::class.java)!!
                    onSuccessListener(diligencias)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun AdicionarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun AtualizarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun DeletarDiligencia(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
        TODO("Not yet implemented")
    }
}

interface IDiligenciaRepository {
    fun ObterDiligencias(onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligencia(id: String, onSuccessListener: (diligencia: Diligencia) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligenciasPorProcesso(numeroProcesso: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligenciasPorAdvogado(emailAdvogado: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AdicionarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
    fun AtualizarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
    fun DeletarDiligencia(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
}