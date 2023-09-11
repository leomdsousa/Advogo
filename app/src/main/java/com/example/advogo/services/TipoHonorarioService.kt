package com.example.advogo.services

import com.example.advogo.models.TiposHonorarios
import com.example.advogo.repositories.ITipoHonorarioRepository
import javax.inject.Inject

class TipoHonorarioService {
    @Inject lateinit var repository: ITipoHonorarioRepository


    fun obterProcessosHonorarios(
        onSuccessListener: (List<TiposHonorarios>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosHonorarios(onSuccessListener, onFailureListener)
    }

    fun obterTipoParte(
        id: String,
        onSuccessListener: (TiposHonorarios) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterTipoParte(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessosHonorarios(): List<TiposHonorarios>? {
        return repository.obterProcessosHonorarios()
    }

    suspend fun obterTipoParte(id: String): TiposHonorarios? {
        return repository.obterTipoParte(id)
    }

    fun adicionarTipoParte(
        model: TiposHonorarios,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.adicionarTipoParte(model, onSuccessListener, onFailureListener)
    }

    fun atualizarTipoParte(
        model: TiposHonorarios,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.atualizarTipoParte(model, onSuccessListener, onFailureListener)
    }

    fun deletarTipoParte(
        id: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.deletarTipoParte(id, onSuccessListener, onFailureListener)
    }
}