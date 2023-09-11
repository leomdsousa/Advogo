package com.example.advogo.services

import com.example.advogo.models.ProcessoTipo
import com.example.advogo.repositories.IProcessoTipoRepository
import javax.inject.Inject

class ProcessoTipoService {
    @Inject lateinit var repository: IProcessoTipoRepository

    fun adicionarProcessoTipo(
        model: ProcessoTipo,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.adicionarProcessoTipo(model, onSuccessListener, onFailureListener)
    }

    fun atualizarProcessoTipo(
        model: ProcessoTipo,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.atualizarProcessoTipo(model, onSuccessListener, onFailureListener)
    }

    fun deletarProcessoTipo(
        id: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.deletarProcessoTipo(id, onSuccessListener, onFailureListener)
    }

    fun obterProcessosTipos(
        onSuccessListener: (List<ProcessoTipo>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosTipos(onSuccessListener, onFailureListener)
    }

    fun obterProcessoTipo(
        id: String,
        onSuccessListener: (processoTipo: ProcessoTipo) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoTipo(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessosTiposSuspend(): List<ProcessoTipo>? {
        return repository.obterProcessosTipos()
    }

    suspend fun obterProcessoTipoSuspend(id: String): ProcessoTipo? {
        return repository.obterProcessoTipo(id)
    }
}