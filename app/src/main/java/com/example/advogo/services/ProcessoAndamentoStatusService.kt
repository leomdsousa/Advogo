package com.example.advogo.services

import com.example.advogo.models.ProcessoStatusAndamento
import com.example.advogo.repositories.IProcessoStatusAndamentoRepository
import javax.inject.Inject

class ProcessoAndamentoStatusService {
    @Inject lateinit var repository: IProcessoStatusAndamentoRepository

    fun adicionarProcessoStatusAndamento(
        model: ProcessoStatusAndamento,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.adicionarProcessoStatusAndamento(model, onSuccessListener, onFailureListener)
    }

    fun atualizarProcessoStatusAndamento(
        model: ProcessoStatusAndamento,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.atualizarProcessoStatusAndamento(model, onSuccessListener, onFailureListener)
    }

    fun deletarProcessoStatusAndamento(
        id: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.deletarProcessoStatusAndamento(id, onSuccessListener, onFailureListener)
    }

    fun obterProcessoStatusAndamentos(
        onSuccessListener: (List<ProcessoStatusAndamento>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoStatusAndamentos(onSuccessListener, onFailureListener)
    }

    fun obterProcessoStatusAndamento(
        id: String,
        onSuccessListener: (processoStatusAndamentos: ProcessoStatusAndamento) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoStatusAndamento(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessoStatusAndamentosSuspend(): List<ProcessoStatusAndamento>? {
        return repository.obterProcessoStatusAndamentos()
    }

    suspend fun obterProcessoStatusAndamentoSuspend(id: String): ProcessoStatusAndamento? {
        return repository.obterProcessoStatusAndamento(id)
    }
}