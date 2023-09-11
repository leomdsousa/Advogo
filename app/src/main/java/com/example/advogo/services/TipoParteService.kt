package com.example.advogo.services

import com.example.advogo.models.TiposPartes
import com.example.advogo.repositories.ITipoParteRepository
import javax.inject.Inject

class TipoParteService {
    @Inject lateinit var repository: ITipoParteRepository


    fun obterProcessosTipos(
        onSuccessListener: (List<TiposPartes>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosTipos(onSuccessListener, onFailureListener)
    }

    fun obterTipoParte(
        id: String,
        onSuccessListener: (TiposPartes) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterTipoParte(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessosTipos(): List<TiposPartes>? {
        return repository.obterProcessosTipos()
    }

    suspend fun obterTipoParte(id: String): TiposPartes? {
        return repository.obterTipoParte(id)
    }

    fun adicionarTipoParte(
        model: TiposPartes,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.adicionarTipoParte(model, onSuccessListener, onFailureListener)
    }

    fun atualizarTipoParte(
        model: TiposPartes,
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