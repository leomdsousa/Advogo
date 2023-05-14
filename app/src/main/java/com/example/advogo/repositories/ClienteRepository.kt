package com.example.advogo.repositories

import com.example.advogo.models.Cliente
import com.example.advogo.models.Processo
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class ClienteRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IClienteRepository {
    override fun ObterClientes(onSuccessListener: (lista: List<Cliente>) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val clientes = document.toObjects(Cliente::class.java)
                    onSuccessListener(clientes)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun ObterCliente(id: String, onSuccessListener: (cliente: Cliente) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val cliente = document.toObject(Cliente::class.java)!!
                    onSuccessListener(cliente)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun ObterClientePorEmail(email: String, onSuccessListener: (cliente: Cliente) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .whereEqualTo(Constants.CLIENTES_EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val cliente = document.first().toObject(Cliente::class.java)
                    onSuccessListener(cliente)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun AdicionarCliente(model: Cliente, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .document(getCurrentUserId())
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener
            }
            .addOnFailureListener {
                onFailureListener
            }
    }

    override fun AtualizarCliente(model: Cliente, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .document(getCurrentUserId())
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener
            }
            .addOnFailureListener {
                onFailureListener
            }
    }

    override fun DeletarCliente(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .document(getCurrentUserId())
            .delete()
            .addOnSuccessListener {
                onSuccessListener
            }
            .addOnFailureListener {
                onFailureListener
            }
    }

    private fun getCurrentUserId(): String {
        val user = FirebaseAuth.getInstance().currentUser

        return if(user != null) {
            FirebaseAuth.getInstance().currentUser!!.uid
        } else {
            ""
        }
    }
}

interface IClienteRepository {
    fun ObterClientes(onSuccessListener: (lista: List<Cliente>) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun ObterCliente(id: String, onSuccessListener: (cliente: Cliente) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun ObterClientePorEmail(email: String, onSuccessListener: (cliente: Cliente) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun AdicionarCliente(model: Cliente, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit)
    fun AtualizarCliente(model: Cliente, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit)
    fun DeletarCliente(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit)
}