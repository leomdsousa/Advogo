package com.example.advogo.services

import com.example.advogo.models.DiligenciaTipo
import com.example.advogo.repositories.IDiligenciaTipoRepository
import javax.inject.Inject

class DiligenciaTipoService {
    @Inject lateinit var repository: IDiligenciaTipoRepository


    fun adicionarDiligenciaTipo(
        model: DiligenciaTipo,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.adicionarDiligenciaTipo(model, onSuccessListener, onFailureListener)
    }

    fun atualizarDiligenciaTipo(
        model: DiligenciaTipo,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.atualizarDiligenciaTipo(model, onSuccessListener, onFailureListener)
    }

    fun deletarDiligenciaTipo(
        id: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.deletarDiligenciaTipo(id, onSuccessListener, onFailureListener)
    }

    fun obterDiligenciasTipos(
        onSuccessListener: (List<DiligenciaTipo>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciasTipos(onSuccessListener, onFailureListener)
    }

    fun obterDiligenciaTipo(
        id: String,
        onSuccessListener: (processoTipo: DiligenciaTipo) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciaTipo(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterDiligenciasTipos(): List<DiligenciaTipo>? {
        return repository.obterDiligenciasTipos()
    }

    suspend fun obterDiligenciaTipo(id: String): DiligenciaTipo? {
        return repository.obterDiligenciaTipo(id)
    }
}