package com.example.advogo.services

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.advogo.models.Processo
import com.example.advogo.models.ProcessoHistorico
import com.example.advogo.repositories.IProcessoHistoricoRepository
import com.google.common.base.Strings
import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ProcessosHistoricoService {
    @Inject lateinit var repository: IProcessoHistoricoRepository

    @RequiresApi(Build.VERSION_CODES.O)
    fun cadastrar(
        processo: Processo,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val historico = ProcessoHistorico(
            obs = "PROCESSO CADASTRADO",
            advogado = processo.advogado,
            status = processo.status,
            tipo = processo.tipo,
            processo = processo.numero,
            data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            dataTimestamp = Timestamp.now()
        )

        val erros = validar(historico)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        repository.adicionarProcessoHistorico(
            historico,
            { onSuccess() },
            { onFailure(listOf("Falha ao salvar histórico do processo.")) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun atualizar(
        processo: Processo,
        alteracoes: String,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val historico = ProcessoHistorico(
            obs = alteracoes,
            advogado = processo.advogado,
            status = processo.status,
            tipo = processo.tipo,
            processo = processo.numero,
            data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            dataTimestamp = Timestamp.now()
        )

        val erros = validar(historico)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        repository.adicionarProcessoHistorico(
            historico,
            { onSuccess() },
            { onFailure(listOf("Falha ao salvar histórico do processo.")) }
        )
    }

    private fun validar(processoHistorico: ProcessoHistorico): List<String> {
        val errors = mutableListOf<String>()

        if (Strings.isNullOrEmpty(processoHistorico.obs)) {
            errors.add("A descrição do histórico é obrigatória.")
        }

        if (Strings.isNullOrEmpty(processoHistorico.processo)) {
            errors.add("O processo do histórico é obrigatório.")
        }

        if (Strings.isNullOrEmpty(processoHistorico.advogado)) {
            errors.add("O advogado do histórico é obrigatório.")
        }

        if (Strings.isNullOrEmpty(processoHistorico.status)) {
            errors.add("O status do processo é obrigatório.")
        }

        if (Strings.isNullOrEmpty(processoHistorico.tipo)) {
            errors.add("O tipo do processo é obrigatório.")
        }

        return errors
    }


    fun obterProcessosHistoricos(
        onSuccessListener: (List<ProcessoHistorico>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosHistoricos(onSuccessListener, onFailureListener)
    }

    fun obterProcessosHistoricosPorProcesso(
        numeroProcesso: String,
        onSuccessListener: (List<ProcessoHistorico>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosHistoricosPorProcesso(numeroProcesso, onSuccessListener, onFailureListener)
    }

    fun obterProcessoHistorico(
        id: String,
        onSuccessListener: (processoHistorico: ProcessoHistorico) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoHistorico(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessosHistoricoPorLista(ids: List<String>): List<ProcessoHistorico>? {
        return repository.obterProcessosHistoricoPorLista(ids)
    }

    suspend fun obterProcessosHistoricosPorProcesso(numeroProcesso: String): List<ProcessoHistorico>? {
        return repository.obterProcessosHistoricosPorProcesso(numeroProcesso)
    }

    suspend fun obterProcessosHistoricos(): List<ProcessoHistorico>? {
        return repository.obterProcessosHistoricos()
    }

    suspend fun obterProcessoHistorico(id: String): ProcessoHistorico? {
        return repository.obterProcessoHistorico(id)
    }
}