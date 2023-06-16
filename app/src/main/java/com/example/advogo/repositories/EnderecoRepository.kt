package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.models.Endereco
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject

class EnderecoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IEnderecoRepository {

    override fun ObterEnderecos(onSuccessListener: (lista: List<Endereco>) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ENDERECOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val enderecos = document.toObjects(Endereco::class.java)
                    onSuccessListener(enderecos)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun ObterEndereco(id: String, onSuccessListener: (process: Endereco) -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ENDERECOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val endereco = document.toObject(Endereco::class.java)!!
                    onSuccessListener(endereco)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun AdicionarEndereco(model: Endereco, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ENDERECOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override fun AtualizarEndereco(model: Endereco, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ENDERECOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }

    override fun DeletarEndereco(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ENDERECOS_TABLE)
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

interface IEnderecoRepository {
    fun ObterEnderecos(onSuccessListener: (lista: List<Endereco>) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun ObterEndereco(id: String, onSuccessListener: (endereco: Endereco) -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AdicionarEndereco(model: Endereco, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun AtualizarEndereco(model: Endereco, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
    fun DeletarEndereco(id: String, onSuccessListener: () -> Unit, onFailureListener: (ex: Exception?) -> Unit)
}