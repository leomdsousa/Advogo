package com.example.advogo.repositories

import com.example.advogo.models.Diligencia
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject


class DiligenciaRepository @Inject constructor(
    //private val firebaseStore: FirebaseFirestore
) : IDiligenciaRepository {
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

    override fun AtualizarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
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

    override fun DeletarDiligencia(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
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

interface IDiligenciaRepository {
    fun ObterDiligencias(onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligencia(id: String, onSuccessListener: (diligencia: Diligencia) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligenciasPorProcesso(numeroProcesso: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterDiligenciasPorAdvogado(emailAdvogado: String, onSuccessListener: (lista: List<Diligencia>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AdicionarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
    fun AtualizarDiligencia(model: Diligencia, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
    fun DeletarDiligencia(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
}