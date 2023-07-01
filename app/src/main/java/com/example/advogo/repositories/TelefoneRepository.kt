package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.models.Telefone
import com.example.advogo.models.TelefoneTipo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import javax.inject.Provider

class TelefoneRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore,
    private val telefoneTipoRepository: TelefoneTipoRepository
): ITelefoneRepository {
    override fun obterTelefones(onSuccessListener: (List<Telefone>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TELEFONES_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val lista = document.toObjects(Telefone::class.java)
                    onSuccessListener(lista)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun obterTelefone(id: String, onSuccessListener: (telefone: Telefone) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TELEFONES_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val telefone = document.toObject(Telefone::class.java)
                    if (telefone != null) {
                        onSuccessListener(telefone)
                    }
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun adicionarTelefone(model: Telefone, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TELEFONES_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override fun atualizarTelefone(model: Telefone, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TELEFONES_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override fun deletarTelefone(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TELEFONES_TABLE)
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

interface ITelefoneRepository {
    fun obterTelefones(onSuccessListener: (List<Telefone>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun obterTelefone(id: String, onSuccessListener: (telefone: Telefone) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun adicionarTelefone(model: Telefone, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun atualizarTelefone(model: Telefone, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun deletarTelefone(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}