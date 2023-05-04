package com.example.advogo.repositories

import com.example.advogo.models.Diligencia
import com.google.firebase.firestore.FirebaseFirestore


class DiligenciaRepository: IDiligenciaRepository {
    private val firebaseStore = FirebaseFirestore.getInstance()

    override fun ObterDiligencias(): ArrayList<Diligencia> {
        TODO("Not yet implemented")
    }

    override fun ObterDiligencia(id: String): Diligencia {
        TODO("Not yet implemented")
    }

    override fun ObterDiligenciasPorProcesso(numeroProcesso: String): ArrayList<Diligencia> {
        TODO("Not yet implemented")
    }

    override fun ObterDiligenciasPorAdvogado(emailAdvogado: String): ArrayList<Diligencia> {
        TODO("Not yet implemented")
    }

    override fun AdicionarDiligencia(model: Diligencia): Diligencia {
        TODO("Not yet implemented")
    }

    override fun AtualizarDiligencia(model: Diligencia): Diligencia {
        TODO("Not yet implemented")
    }

    override fun DeletarDiligencia(id: String): Boolean {
        TODO("Not yet implemented")
    }
}

interface IDiligenciaRepository {
    fun ObterDiligencias(): ArrayList<Diligencia>
    fun ObterDiligencia(id: String): Diligencia
    fun ObterDiligenciasPorProcesso(numeroProcesso: String): ArrayList<Diligencia>
    fun ObterDiligenciasPorAdvogado(emailAdvogado: String): ArrayList<Diligencia>
    fun AdicionarDiligencia(model: Diligencia): Diligencia
    fun AtualizarDiligencia(model: Diligencia): Diligencia
    fun DeletarDiligencia(id: String): Boolean
}