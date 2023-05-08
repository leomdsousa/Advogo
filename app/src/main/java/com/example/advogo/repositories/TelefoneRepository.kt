package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.models.Telefone
import com.example.advogo.models.TelefoneTipo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class TelefoneRepository @Inject constructor(): ITelefoneRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterTelefones(onSuccessListener: OnSuccessListener<List<Telefone>>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.TELEFONES_TABLE)
            .get()
//            .addOnSuccessListener { document ->
//                if (!document.isEmpty) {
//                    val lista = document.toObjects(Telefone::class.java)
//                    onSuccessListener(lista)
//                } else {
//                    onFailureListener(null)
//                }
//            }
//            .addOnFailureListener { exception ->
//                onFailureListener(exception)
//            }
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

//    override fun ObterTelefone(ids: List<String>, onSuccessListener: OnSuccessListener<Telefone>, onFailureListener: OnFailureListener) {
//        firebaseStore
//            .collection(Constants.TELEFONES_TABLE)
//            .whereIn(Constants.TELEFONES_ID, ids)
//            .get()
//    }

    override fun AdicionarTelefone(model: Telefone, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }

    override fun AtualizarTelefone(model: Telefone, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }

    override fun DeletarTelefone(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }
}

interface ITelefoneRepository {
    fun ObterTelefones(onSuccessListener: OnSuccessListener<List<Telefone>>, onFailureListener: OnFailureListener)
    fun ObterTelefone(id: String, onSuccessListener: (telefone: Telefone) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    //fun ObterTelefone(ids: List<String>, onSuccessListener: OnSuccessListener<Telefone>, onFailureListener: OnFailureListener)
    fun AdicionarTelefone(model: Telefone, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
    fun AtualizarTelefone(model: Telefone, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
    fun DeletarTelefone(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
}