package com.example.advogo.repositories

import com.example.advogo.models.Processo
import com.google.firebase.firestore.FirebaseFirestore

class ProcessoRepository: IProcessoRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterProcessos(): ArrayList<Processo> {
        TODO("Not yet implemented")
    }

    override fun ObterProcesso(id: String): Processo {
        TODO("Not yet implemented")
    }

    override fun ObterProcessoPorNumero(numero: String): Processo {
        TODO("Not yet implemented")
    }

    override fun AdicionarProcesso(model: Processo): Processo {
        TODO("Not yet implemented")
    }

    override fun AtualizarProcesso(model: Processo): Processo {
        TODO("Not yet implemented")
    }

    override fun DeletarProcesso(id: String): Boolean {
        TODO("Not yet implemented")
    }
}

interface IProcessoRepository {
    fun ObterProcessos(): ArrayList<Processo>
    fun ObterProcesso(id: String): Processo
    fun ObterProcessoPorNumero(numero: String): Processo
    fun AdicionarProcesso(model: Processo): Processo
    fun AtualizarProcesso(model: Processo): Processo
    fun DeletarProcesso(id: String): Boolean
}