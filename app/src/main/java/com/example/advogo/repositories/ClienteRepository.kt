package com.example.advogo.repositories

import com.example.advogo.models.Cliente
import com.example.advogo.utils.constants.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ClienteRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IClienteRepository {
    override fun obterClientes(onSuccessListener: (lista: List<Cliente>) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
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
    override fun obterCliente(id: String, onSuccessListener: (cliente: Cliente) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
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
    override fun obterClientesByNomeContains(text: String, onSuccessListener: (lista: List<Cliente>) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .orderBy(Constants.CLIENTES_NOME)
            .whereGreaterThanOrEqualTo(Constants.CLIENTES_NOME, text)
            .whereLessThanOrEqualTo(Constants.CLIENTES_NOME, "${text}\uF7FF")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val clientes = documents.toObjects(Cliente::class.java)!!
                    onSuccessListener(clientes)
                } else {
                    onSuccessListener(emptyList())
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun obterClientes(): List<Cliente>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val cliente = document.toObjects(Cliente::class.java)!!
                    continuation.resume(cliente)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterCliente(id: String): Cliente? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val cliente = document.toObject(Cliente::class.java)!!
                    continuation.resume(cliente)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override fun obterClientePorEmail(email: String, onSuccessListener: (cliente: Cliente) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
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
    override fun adicionarCliente(model: Cliente, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarCliente(model: Cliente, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarCliente(id: String, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.CLIENTES_TABLE)
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

interface IClienteRepository {
    fun obterClientes(onSuccessListener: (lista: List<Cliente>) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun obterCliente(id: String, onSuccessListener: (cliente: Cliente) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun obterClientesByNomeContains(text: String, onSuccessListener: (lista: List<Cliente>) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun obterClientePorEmail(email: String, onSuccessListener: (cliente: Cliente) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun adicionarCliente(model: Cliente, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun atualizarCliente(model: Cliente, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun deletarCliente(id: String, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit)

    suspend fun obterClientes(): List<Cliente>?
    suspend fun obterCliente(id: String): Cliente?
}