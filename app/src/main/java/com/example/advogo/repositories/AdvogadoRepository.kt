package com.example.advogo.repositories

import com.example.advogo.models.Advogado
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

class AdvogadoRepository @Inject constructor(
    private val firebaseStore: FirebaseFirestore
): IAdvogadoRepository {
    override fun ObterAdvogados(onSuccessListener: OnSuccessListener<List<Advogado>>, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .get()
    }

    override fun ObterAdvogado(id: String, onSuccessListener: (advogado: Advogado) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(getCurrentUserId())
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

    override fun ObterAdvogadoPorEmail(email: String, onSuccessListener: (advogado: Advogado) -> Unit, onFailureListener: (exception: Exception?) -> Unit) {
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

    override fun AdicionarAdvogado(model: Advogado, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(getCurrentUserId())
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener
            }
            .addOnFailureListener {
                onFailureListener
            }
    }

    override fun AtualizarAdvogado(model: Advogado, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(getCurrentUserId())
            .set(model, SetOptions.merge())
            .addOnSuccessListener {
                onSuccessListener
            }
            .addOnFailureListener {
                onFailureListener
            }
    }

    override fun DeletarAdvogado(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
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

interface IAdvogadoRepository {
    fun ObterAdvogados(onSuccessListener: OnSuccessListener<List<Advogado>>, onFailureListener: (exception: Exception?) -> Unit)
    fun ObterAdvogado(id: String, onSuccessListener: (advogado: Advogado) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun ObterAdvogadoPorEmail(email: String, onSuccessListener: (advogado: Advogado) -> Unit, onFailureListener: (exception: Exception?) -> Unit)
    fun AdicionarAdvogado(model: Advogado, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit)
    fun AtualizarAdvogado(model: Advogado, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit)
    fun DeletarAdvogado(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: (exception: Exception?) -> Unit)
}