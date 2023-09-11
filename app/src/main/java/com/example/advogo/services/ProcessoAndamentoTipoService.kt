package com.example.advogo.services

import com.example.advogo.models.ProcessoTipoAndamento
import com.example.advogo.repositories.IProcessoTipoAndamentoRepository
import javax.inject.Inject

class ProcessoAndamentoTipoService {
    @Inject lateinit var repository: IProcessoTipoAndamentoRepository


    fun adicionarProcessoTipoAndamento(
        model: ProcessoTipoAndamento,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.adicionarProcessoTipoAndamento(model, onSuccessListener, onFailureListener)
    }

    fun atualizarProcessoTipoAndamento(
        model: ProcessoTipoAndamento,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.atualizarProcessoTipoAndamento(model, onSuccessListener, onFailureListener)
    }

    fun deletarProcessoTipoAndamento(
        id: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.deletarProcessoTipoAndamento(id, onSuccessListener, onFailureListener)
    }

    fun obterProcessoTipoAndamentos(
        onSuccessListener: (List<ProcessoTipoAndamento>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoTipoAndamentos(onSuccessListener, onFailureListener)
    }

    fun obterProcessoTipoAndamento(
        id: String,
        onSuccessListener: (processoTipoAndamentos: ProcessoTipoAndamento) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoTipoAndamento(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessoTipoAndamentosSuspend(): List<ProcessoTipoAndamento>? {
        return repository.obterProcessoTipoAndamentos()
    }

    suspend fun obterProcessoTipoAndamentoSuspend(id: String): ProcessoTipoAndamento? {
        return repository.obterProcessoTipoAndamento(id)
    }
}