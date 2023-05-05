package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class AdvogadoRepository @Inject constructor(): IAdvogadoRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override suspend fun ObterAdvogados(): ArrayList<Advogado> {
        TODO("Not yet implemented")
    }

    override suspend fun ObterAdvogado(id: String): Advogado {
        TODO("Not yet implemented")
    }

    override suspend fun ObterAdvogadoPorEmail(email: String): Advogado {
        TODO("Not yet implemented")
    }

    override suspend fun AdicionarAdvogado(model: Advogado): Advogado {
        TODO("Not yet implemented")
    }

    override suspend fun AtualizarAdvogado(model: Advogado): Advogado {
        TODO("Not yet implemented")
    }

    override suspend fun DeletarAdvogado(id: String): Boolean {
        TODO("Not yet implemented")
    }
}

interface IAdvogadoRepository {
    suspend fun ObterAdvogados(): ArrayList<Advogado>
    suspend fun ObterAdvogado(id: String): Advogado
    suspend fun ObterAdvogadoPorEmail(email: String): Advogado
    suspend fun AdicionarAdvogado(model: Advogado): Advogado
    suspend fun AtualizarAdvogado(model: Advogado): Advogado
    suspend fun DeletarAdvogado(id: String): Boolean
}