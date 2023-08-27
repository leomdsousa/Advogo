package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.models.Cliente
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AdvogadoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IAdvogadoRepository {
    override fun obterAdvogados(onSuccessListener: (lista: List<Advogado>) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val advogados = document.toObjects(Advogado::class.java)
                    onSuccessListener(advogados)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }
    override fun obterAdvogado(id: String, onSuccessListener: (advogado: Advogado) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val advogado = document.toObject(Advogado::class.java)!!
                    onSuccessListener(advogado)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override suspend fun obterAdvogados(): List<Advogado>? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val cliente = document.toObjects(Advogado::class.java)!!
                    continuation.resume(cliente)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }
    override suspend fun obterAdvogado(id: String): Advogado? = suspendCoroutine { continuation ->
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val cliente = document.toObject(Advogado::class.java)!!
                    continuation.resume(cliente)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override fun obterAdvogadoPorEmail(email: String, onSuccessListener: (advogado: Advogado) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .whereEqualTo(Constants.ADVOGADOS_EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val advogado = document.first().toObject(Advogado::class.java)
                    onSuccessListener(advogado)
                } else {
                    onFailureListener(null)
                }
            }
            .addOnFailureListener { exception ->
                onFailureListener(exception)
            }
    }

    override fun adicionarAdvogado(model: Advogado, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .add(model)
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun atualizarAdvogado(model: Advogado, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(model.id)
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
            }
    }
    override fun deletarAdvogado(id: String, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(id)
            .delete()
            .addOnSuccessListener {
                onSuccessListener()
            }
            .addOnFailureListener {
                onFailureListener(it)
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

interface IAdvogadoRepository {
    fun obterAdvogados(onSuccessListener: (lista: List<Advogado>) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun obterAdvogado(id: String, onSuccessListener: (advogado: Advogado) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun obterAdvogadoPorEmail(email: String, onSuccessListener: (advogado: Advogado) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun adicionarAdvogado(model: Advogado, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun atualizarAdvogado(model: Advogado, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun deletarAdvogado(id: String, onSuccessListener: () -> Unit, onFailureListener: (exception: Exception?) -> Unit)

    suspend fun obterAdvogados(): List<Advogado>?
    suspend fun obterAdvogado(id: String): Advogado?
}