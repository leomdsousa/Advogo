package com.example.advogo.repositories

import com.example.advogo.models.Anexo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Provider


class AnexoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore,
) : IAnexoRepository {
    override fun ObterAnexos(onSuccessListener: (lista: List<Anexo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val anexos = document.toObjects(Anexo::class.java)
                    onSuccessListener(anexos)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun ObterAnexo(id: String, onSuccessListener: (Anexo: Anexo) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val anexo = document.toObject(Anexo::class.java)!!
                    onSuccessListener(anexo)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun AdicionarAnexo(model: Anexo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun DeletarAnexo(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ANEXOS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
}

interface IAnexoRepository {
    fun ObterAnexos(onSuccessListener: (lista: List<Anexo>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterAnexo(id: String, onSuccessListener: (Anexo: Anexo) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AdicionarAnexo(model: Anexo, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun DeletarAnexo(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}