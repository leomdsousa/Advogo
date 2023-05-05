package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.example.advogo.utils.Constants
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AdvogadoRepository @Inject constructor(): IAdvogadoRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterAdvogados(onSuccessListener: OnSuccessListener<List<Advogado>>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .get()
    }

    override fun ObterAdvogado(id: String, onSuccessListener: OnSuccessListener<Advogado>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .document(getCurrentUserId())
            .get()
    }

    override fun ObterAdvogadoPorEmail(email: String, onSuccessListener: OnSuccessListener<Advogado>, onFailureListener: OnFailureListener) {
        firebaseStore
            .collection(Constants.ADVOGADOS_TABLE)
            .whereEqualTo(Constants.ADVOGADOS_EMAIL, email)
            .get()
    }

    override fun AdicionarAdvogado(model: Advogado, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }

    override fun AtualizarAdvogado(model: Advogado, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
    }

    override fun DeletarAdvogado(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener) {
        TODO("Not yet implemented")
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
    fun ObterAdvogados(onSuccessListener: OnSuccessListener<List<Advogado>>, onFailureListener: OnFailureListener)
    fun ObterAdvogado(id: String, onSuccessListener: OnSuccessListener<Advogado>, onFailureListener: OnFailureListener)
    fun ObterAdvogadoPorEmail(email: String, onSuccessListener: OnSuccessListener<Advogado>, onFailureListener: OnFailureListener)
    fun AdicionarAdvogado(model: Advogado, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
    fun AtualizarAdvogado(model: Advogado, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
    fun DeletarAdvogado(id: String, onSuccessListener: OnSuccessListener<Unit>, onFailureListener: OnFailureListener)
}