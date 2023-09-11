package com.example.advogo.services

import com.example.advogo.models.ProcessoStatus
import com.example.advogo.repositories.IProcessoStatusRepository
import javax.inject.Inject

class ProcessoStatusService {
    @Inject lateinit var repository: IProcessoStatusRepository

    fun adicionarProcessoStatus(
        model: ProcessoStatus,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.adicionarProcessoStatus(model, onSuccessListener, onFailureListener)
    }

    fun atualizarProcessoStatus(
        model: ProcessoStatus,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.atualizarProcessoStatus(model, onSuccessListener, onFailureListener)
    }

    fun deletarProcessoStatus(
        id: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.deletarProcessoStatus(id, onSuccessListener, onFailureListener)
    }

    fun obterProcessosStatus(
        onSuccessListener: (List<ProcessoStatus>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosStatus(onSuccessListener, onFailureListener)
    }

    fun obterProcessoStatus(
        id: String,
        onSuccessListener: (processoStatus: ProcessoStatus) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoStatus(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessoStatusSuspend(): List<ProcessoStatus>? {
        return repository.obterProcessoStatus()
    }

    suspend fun obterProcessoStatusSuspend(id: String): ProcessoStatus? {
        return repository.obterProcessoStatus(id)
    }
}