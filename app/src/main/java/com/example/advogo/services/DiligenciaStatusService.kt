package com.example.advogo.services

import com.example.advogo.models.DiligenciaStatus
import com.example.advogo.repositories.IDiligenciaStatusRepository
import javax.inject.Inject

class DiligenciaStatusService {
    @Inject lateinit var repository: IDiligenciaStatusRepository


    fun adicionarDiligenciaStatus(
        model: DiligenciaStatus,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.adicionarDiligenciaStatus(model, onSuccessListener, onFailureListener)
    }

    fun atualizarDiligenciaStatus(
        model: DiligenciaStatus,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.atualizarDiligenciaStatus(model, onSuccessListener, onFailureListener)
    }

    fun deletarDiligenciaStatus(
        id: String,
        onSuccessListener: () -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.deletarDiligenciaStatus(id, onSuccessListener, onFailureListener)
    }

    fun obterDiligenciasStatus(
        onSuccessListener: (List<DiligenciaStatus>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciasStatus(onSuccessListener, onFailureListener)
    }

    fun obterDiligenciaStatus(
        id: String,
        onSuccessListener: (processoStatus: DiligenciaStatus) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciaStatus(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterDiligenciasStatus(): List<DiligenciaStatus>? {
        return repository.obterDiligenciasStatus()
    }

    suspend fun obterDiligenciaStatus(id: String): DiligenciaStatus? {
        return repository.obterDiligenciaStatus(id)
    }
}