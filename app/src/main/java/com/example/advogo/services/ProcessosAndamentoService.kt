package com.example.advogo.services

import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.example.advogo.models.ProcessoAndamento
import com.example.advogo.repositories.IProcessoAndamentoRepository
import com.example.advogo.utils.UserUtils.getCurrentUserID
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.google.firebase.Timestamp
import javax.inject.Inject

class ProcessosAndamentoService {
    @Inject lateinit var repository: IProcessoAndamentoRepository


    @RequiresApi(Build.VERSION_CODES.O)
    fun cadastrar(
        andamento: ProcessoAndamento,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val input = ProcessoAndamento(
            id = "",
            descricao = andamento.descricao,
            advogado = getCurrentUserID(),
            processo = andamento.processo,
            tipo = andamento.tipo,
            status = andamento.status,
            data = andamento.data,
            dataTimestamp = Timestamp.now()
        )

        input.dataTimestamp = Timestamp(input.data!!.fromUSADateStringToDate())

        val erros = validar(input)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        repository.adicionarProcessoAndamento(
            input,
            { onSuccess() },
            { onFailure(listOf("Falha ao salvar histórico do processo.")) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun atualizar(
        andamento: ProcessoAndamento,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val input = ProcessoAndamento(
            id = andamento.id,
            descricao = andamento.descricao,
            advogado = getCurrentUserID(),
            processo = andamento.processo,
            tipo = andamento.tipo,
            status = andamento.status,
            data = andamento.data,
        )

        input.dataTimestamp = Timestamp(input.data!!.fromUSADateStringToDate())

        val erros = validar(input)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        repository.atualizarProcessoAndamento(
            input,
            { onSuccess() },
            { onFailure(listOf("Falha ao salvar histórico do processo.")) }
        )
    }

    private fun validar(andamento: ProcessoAndamento): List<String> {
        val errors = mutableListOf<String>()

        if (TextUtils.isEmpty(andamento.descricao)) {
            errors.add("A descrição do andamento é obrigatória.")
        }

        if (TextUtils.isEmpty(andamento.data)) {
            errors.add("A data do andamento é obrigatória.")
        }

        if (TextUtils.isEmpty(andamento.tipo)) {
            errors.add("O tipo do andamento é obrigatório.")
        }

        if (TextUtils.isEmpty(andamento.status)) {
            errors.add("O status do andamento é obrigatório.")
        }

        return errors
    }


    fun obterProcessosAndamentos(
        onSuccessListener: (List<ProcessoAndamento>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosAndamentos(onSuccessListener, onFailureListener)
    }

    fun obterProcessosAndamentosPorProcesso(
        numeroProcesso: String,
        onSuccessListener: (List<ProcessoAndamento>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosAndamentosPorProcesso(numeroProcesso, onSuccessListener, onFailureListener)
    }

    fun obterProcessoAndamento(
        id: String,
        onSuccessListener: (ProcessoAndamento: ProcessoAndamento) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoAndamento(id, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessosAndamentos(): List<ProcessoAndamento>? {
        return repository.obterProcessosAndamentos()
    }

    suspend fun obterProcessoAndamento(id: String): ProcessoAndamento? {
        return repository.obterProcessoAndamento(id)
    }

    suspend fun obterAndamentosPorLista(ids: List<String>): List<ProcessoAndamento>? {
        return repository.obterAndamentosPorLista(ids)
    }

    suspend fun obterAndamentosPorProcesso(numeroProcesso: String): List<ProcessoAndamento>? {
        return repository.obterAndamentosPorProcesso(numeroProcesso)
    }
}