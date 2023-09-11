package com.example.advogo.services

import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.example.advogo.models.Diligencia
import com.example.advogo.models.DiligenciaHistorico
import com.example.advogo.repositories.IDiligenciaHistoricoRepository
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DiligenciasHistoricoService {
    @Inject lateinit var repository: IDiligenciaHistoricoRepository

    @RequiresApi(Build.VERSION_CODES.O)
    fun cadastrar(
        diligencia: Diligencia,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val historico = DiligenciaHistorico(
            obs = "DILIGÊNCIA CADASTRADA",
            advogado = diligencia.advogado,
            status = diligencia.status,
            tipo = diligencia.tipo,
            diligencia = diligencia.id,
            data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            dataTimestamp = Timestamp.now()
        )

        val erros = validar(historico)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        repository.adicionarDiligenciaHistorico(
            historico,
            { onSuccess() },
            { onFailure(listOf("Falha ao salvar histórico da diligência.")) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun atualizar(
        input: Diligencia,
        alteracoes: String,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val historico = DiligenciaHistorico(
            obs = alteracoes,
            advogado = input.advogado,
            status = input.status,
            tipo = input.tipo,
            diligencia = input.id,
            data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            dataTimestamp = Timestamp.now()
        )

        val erros = validar(historico)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        repository.adicionarDiligenciaHistorico(
            historico,
            { onSuccess() },
            { onFailure(listOf("Falha ao salvar histórico da diligência.")) }
        )
    }

    private fun validar(historico: DiligenciaHistorico): List<String> {
        val errors = mutableListOf<String>()

        if (TextUtils.isEmpty(historico.obs)) {
            errors.add("A descrição do histórico é obrigatória.")
        }

        if (TextUtils.isEmpty(historico.diligencia)) {
            errors.add("A diligência do histórico é obrigatória.")
        }

        if (TextUtils.isEmpty(historico.advogado)) {
            errors.add("O advogado do histórico é obrigatório.")
        }

        if (TextUtils.isEmpty(historico.status)) {
            errors.add("O status da diligência é obrigatório.")
        }

        if (TextUtils.isEmpty(historico.tipo)) {
            errors.add("O tipo da diligência é obrigatório.")
        }

        return errors
    }

    fun obterDiligenciasHistoricos(
        onSuccessListener: (List<DiligenciaHistorico>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciasHistoricos(onSuccessListener, onFailureListener)
    }

    fun obterDiligenciasHistoricosPorDiligencia(
        id: String,
        onSuccessListener: (List<DiligenciaHistorico>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciasHistoricosPorDiligencia(id, onSuccessListener, onFailureListener)
    }

    fun obterDiligenciaHistorico(
        id: String,
        onSuccessListener: (processoHistorico: DiligenciaHistorico) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciaHistorico(id, onSuccessListener, onFailureListener)
    }


    suspend fun obterDiligenciasHistoricoPorLista(ids: List<String>): List<DiligenciaHistorico>? {
        return repository.obterDiligenciasHistoricoPorLista(ids)
    }

    suspend fun obterDiligenciasHistoricoPorDiligencia(id: String): List<DiligenciaHistorico>? {
        return repository.obterDiligenciasHistoricoPorDiligencia(id)
    }

    suspend fun obterDiligenciasHistoricos(): List<DiligenciaHistorico>? {
        return repository.obterDiligenciasHistoricos()
    }

    suspend fun obterDiligenciaHistorico(id: String): DiligenciaHistorico? {
        return repository.obterDiligenciaHistorico(id)
    }
}