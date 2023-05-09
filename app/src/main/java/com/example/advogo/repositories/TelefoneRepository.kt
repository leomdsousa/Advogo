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
    override fun ObterTelefones(onSuccessListener: (List<Telefone>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
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

    override fun ObterTelefone(id: String, onSuccessListener: (telefone: Telefone) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.TELEFONES_TABLE)
            .whereEqualTo(Constants.TELEFONES_ID, id)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val telefone = document.first().toObject(Telefone::class.java)
                    onSuccessListener(telefone)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun AdicionarTelefone(model: Telefone, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
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

    override fun AtualizarTelefone(model: Telefone, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
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

    override fun DeletarTelefone(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit) {
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

interface ITelefoneRepository {
    fun ObterTelefones(onSuccessListener: (List<Telefone>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterTelefone(id: String, onSuccessListener: (telefone: Telefone) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AdicionarTelefone(model: Telefone, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
    fun AtualizarTelefone(model: Telefone, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
    fun DeletarTelefone(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (ex: Exception?) -> Unit)
}