package com.example.advogo.repositories

import com.example.advogo.models.Advogado
import com.google.firebase.firestore.FirebaseFirestore

class AdvogadoRepository: IAdvogadoRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterAdvogados(): ArrayList<Advogado> {
        TODO("Not yet implemented")
    }

    override fun ObterAdvogado(id: String): Advogado {
        TODO("Not yet implemented")
    }

    override fun ObterAdvogadoPorEmail(email: String): Advogado {
        TODO("Not yet implemented")
    }

    override fun AdicionarAdvogado(model: Advogado): Advogado {
        TODO("Not yet implemented")
    }

    override fun AtualizarAdvogado(model: Advogado): Advogado {
        TODO("Not yet implemented")
    }

    override fun DeletarAdvogado(id: String): Boolean {
        TODO("Not yet implemented")
    }
}

interface IAdvogadoRepository {
    fun ObterAdvogados(): ArrayList<Advogado>
    fun ObterAdvogado(id: String): Advogado
    fun ObterAdvogadoPorEmail(email: String): Advogado
    fun AdicionarAdvogado(model: Advogado): Advogado
    fun AtualizarAdvogado(model: Advogado): Advogado
    fun DeletarAdvogado(id: String): Boolean
}