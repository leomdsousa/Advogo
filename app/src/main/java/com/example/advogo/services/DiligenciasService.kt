package com.example.advogo.services

import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.example.advogo.models.Diligencia
import com.example.advogo.models.DiligenciaHistorico
import com.example.advogo.repositories.IDiligenciaHistoricoRepository
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.google.firebase.Timestamp
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DiligenciaService {
    @Inject lateinit var repository: IDiligenciaRepository
    @Inject lateinit var repositoryHistorico: IDiligenciaHistoricoRepository

    @RequiresApi(Build.VERSION_CODES.O)
    fun cadastrar(
        diligencia: Diligencia,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(diligencia)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        repository.adicionarDiligencia(
            diligencia,
            { generatedId ->
                val historico = DiligenciaHistorico(
                    obs = "DILIGÊNCIA CADASTRADA",
                    advogado = diligencia.advogado,
                    status = diligencia.status,
                    tipo = diligencia.tipo,
                    diligencia = generatedId,
                    data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    dataTimestamp = Timestamp.now()
                )

                repositoryHistorico.adicionarDiligenciaHistorico(
                    historico,
                    { onSuccess() },
                    { onFailure(listOf("Falha ao salvar histórico da diligência.")) }
                )
            },
            { onFailure(listOf("Falha ao salvar diligência.")) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun atualizar(
        diligencia: Diligencia,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(diligencia)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val diligenciaDetalhesDeferred = async { repository.obterDiligencia(diligencia.id) }
            val diligenciaAtual = diligenciaDetalhesDeferred.await()!!

            val alteracoes = formatarAlteracoes(diligencia, diligenciaAtual)

            val input = Diligencia(
                id = diligenciaAtual.id,
                descricao = diligencia.descricao.takeIf { it!!.isNotEmpty() } ?: diligenciaAtual.descricao,
                data = if (diligencia.data != diligenciaAtual.data) diligencia.data else diligenciaAtual.data,
                dataCriacao = diligenciaAtual.dataCriacao,
                dataCriacaoTimestamp = diligenciaAtual.dataCriacaoTimestamp,
                dataAlteracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                dataAlteracaoTimestamp = Timestamp.now(),
                status = if (diligencia.status != diligenciaAtual.status) diligencia.status else diligenciaAtual.status,
                tipo = if (diligencia.tipo != diligenciaAtual.tipo) diligencia.tipo else diligenciaAtual.tipo,
                endereco = if (diligencia.endereco != diligenciaAtual.endereco) diligencia.endereco else diligenciaAtual.endereco,
                enderecoLat = diligencia.enderecoLat,
                enderecoLong = diligencia.enderecoLong,
                processo = if (diligencia.processo != diligenciaAtual.processo) diligencia.processo else diligenciaAtual.processo,
                advogado = if (diligencia.advogado != diligenciaAtual.advogado) diligencia.advogado else diligenciaAtual.advogado,
                historico = diligencia.historico
            )

            input.dataTimestamp = Timestamp(input.data!!.fromUSADateStringToDate())

            repository.atualizarDiligencia(
                input,
                {
                    val historico = DiligenciaHistorico(
                        obs = alteracoes,
                        advogado = input.advogado,
                        status = input.status,
                        tipo = input.tipo,
                        diligencia = input.id,
                        data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        dataTimestamp = Timestamp.now()
                    )

                    repositoryHistorico.adicionarDiligenciaHistorico(
                        historico,
                        { onSuccess() },
                        { onFailure(listOf("Falha ao salvar histórico da diligência.")) }
                    )
                },
                { onFailure(listOf("Falha ao salvar diligência.")) }
            )
        }
    }

    private fun validar(diligencia: Diligencia): List<String> {
        val erros = mutableListOf<String>()

        if (TextUtils.isEmpty(diligencia.descricao)) {
            erros.add("Campo 'Descrição' é obrigatório.")
        }

        if (TextUtils.isEmpty(diligencia.data)) {
            erros.add("Campo 'Data' é obrigatório.")
        }

        if (TextUtils.isEmpty(diligencia.endereco)) {
            erros.add("Campo 'Endereço' é obrigatório.")
        }

        if (TextUtils.isEmpty(diligencia.tipo)) {
            erros.add("Campo 'Tipo' é obrigatório.")
        }

        if (TextUtils.isEmpty(diligencia.status)) {
            erros.add("Campo 'Status' é obrigatório.")
        }

        if (TextUtils.isEmpty(diligencia.advogado)) {
            erros.add("Campo 'Advogado' é obrigatório.")
        }

        return erros
    }

    private fun formatarAlteracoes(novaDiligencia: Diligencia, atualDiligencia: Diligencia): String {
        var retorno = "DILIGÊNCIA ATUALIZADA"

        if (!novaDiligencia.status.equals(atualDiligencia.status))
            retorno += "\nStatus: DE ${atualDiligencia.status} para ${novaDiligencia.status}"

        if (!novaDiligencia.tipo.equals(atualDiligencia.tipo))
            retorno += "\nTipo: DE ${atualDiligencia.tipo} para ${novaDiligencia.tipo}"

        if (!novaDiligencia.advogado.equals(atualDiligencia.advogado))
            retorno += "\nAdvogado: para ${novaDiligencia.advogado}"

        if (!novaDiligencia.processo.equals(atualDiligencia.processo))
            retorno += "\nProcesso: para ${novaDiligencia.processo}"

        if (!novaDiligencia.endereco.equals(atualDiligencia.endereco))
            retorno += "\nEndereço para: ${novaDiligencia.endereco}"

        if (!novaDiligencia.descricao.equals(atualDiligencia.descricao))
            retorno += "\nDescrição atualizada"

        return retorno
    }

    fun obterDiligencias(
        onSuccessListener: (lista: List<Diligencia>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligencias(onSuccessListener, onFailureListener)
    }

    fun obterDiligencia(
        id: String,
        onSuccessListener: (diligencia: Diligencia) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligencia(id, onSuccessListener, onFailureListener)
    }

    fun obterDiligenciasByDescricaoContains(
        text: String,
        onSuccessListener: (lista: List<Diligencia>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciasByDescricaoContains(text, onSuccessListener, onFailureListener)
    }

    fun obterDiligenciasPorProcesso(
        numeroProcesso: String,
        onSuccessListener: (lista: List<Diligencia>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciasPorProcesso(numeroProcesso, onSuccessListener, onFailureListener)
    }

    fun obterDiligenciasPorAdvogado(
        emailAdvogado: String,
        onSuccessListener: (lista: List<Diligencia>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterDiligenciasPorAdvogado(emailAdvogado, onSuccessListener, onFailureListener)
    }

    suspend fun obterDiligencia(id: String): Diligencia? {
        return repository.obterDiligencia(id)
    }

    suspend fun obterDiligenciasPorData(data: String): List<Diligencia>? {
        return repository.obterDiligenciasPorData(data)
    }

    suspend fun obterDiligenciasPorData(dataInicio: String, dataFinal: String): List<Diligencia>? {
        return repository.obterDiligenciasPorData(dataInicio, dataFinal)
    }
}
