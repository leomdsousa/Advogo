package com.example.advogo.services

import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.example.advogo.models.Processo
import com.example.advogo.models.ProcessoHistorico
import com.example.advogo.repositories.IProcessoHistoricoRepository
import com.example.advogo.repositories.IProcessoRepository
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.example.advogo.utils.extensions.StringExtensions.removeSpecialCharacters
import com.google.common.io.Files.getFileExtension
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ProcessosService {
    @Inject lateinit var repository: IProcessoRepository
    @Inject lateinit var repositoryHistorico: IProcessoHistoricoRepository

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cadastrar(
        processo: Processo,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(processo)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        val imageUrl = if (processo.imagemSelecionadaURI != null) {
            salvarImagem(processo.imagemSelecionadaURI!!)
        } else {
            processo.imagem
        }

        processo.imagem = imageUrl

        repository.adicionarProcesso(
            processo,
            {
                val historico = ProcessoHistorico(
                    obs = "PROCESSO CADASTRADO",
                    advogado = processo.advogado,
                    status = processo.status,
                    tipo = processo.tipo,
                    processo = processo.numero,
                    data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    dataTimestamp = Timestamp.now()
                )

                repositoryHistorico.adicionarProcessoHistorico(
                    historico,
                    { onSuccess() },
                    { onFailure(listOf("Falha ao salvar histórico do processo.")) }
                )
            },
            { onFailure(listOf("Falha ao salvar processo.")) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun atualizar(
        processo: Processo,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(processo)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val imageUrl = if (processo.imagemSelecionadaURI != null) {
                salvarImagem(processo.imagemSelecionadaURI!!)
            } else {
                processo.imagem
            }

            val processoDetalhesDeferred = async { repository.obterProcesso(processo.id) }
            val processoAtual = processoDetalhesDeferred.await()!!

            val alteracoes = formatarAlteracoes(processo, processoAtual)

            val input = Processo(
                id = processoAtual.id,
                titulo = processo.titulo.takeIf { it!!.isNotEmpty() } ?: processoAtual.titulo,
                descricao = processo.descricao.takeIf { it!!.isNotEmpty() } ?: processoAtual.descricao,
                numero = processo.numero.takeIf { it!!.isNotEmpty() } ?: processoAtual.numero,
                tipo = processo.tipo.takeIf { it!!.isNotEmpty() } ?: processoAtual.tipo,
                status = processo.status.takeIf { it!!.isNotEmpty() } ?: processoAtual.status,
                dataInicio = processoAtual.dataInicio,
                dataCriacao = processoAtual.dataCriacao,
                dataCriacaoTimestamp = processoAtual.dataCriacaoTimestamp,
                dataAlteracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                dataAlteracaoTimestamp = Timestamp.now(),
                imagem = imageUrl,
                cliente = processo.cliente.takeIf { it!!.isNotEmpty() } ?: processoAtual.cliente,
                advogado = processo.advogado.takeIf { it!!.isNotEmpty() } ?: processoAtual.advogado,
                diligencias = processo.diligencias,
                anexos = processo.anexos,
                andamentos = processo.andamentos,
                historico = processo.historico
            )

            input.dataInicioTimestamp = Timestamp(processo.dataInicio!!.fromUSADateStringToDate())

            repository.atualizarProcesso(
                input,
                {
                    val historico = ProcessoHistorico(
                        obs = alteracoes,
                        advogado = input.advogado,
                        status = input.status,
                        tipo = input.tipo,
                        processo = input.numero,
                        data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        dataTimestamp = Timestamp.now()
                    )

                    repositoryHistorico.adicionarProcessoHistorico(
                        historico,
                        { onSuccess() },
                        { onFailure(listOf("Falha ao salvar histórico do processo.")) }
                    )
                },
                { onFailure(listOf("Falha ao salvar processo.")) }
            )
        }

    }

    private suspend fun salvarImagem(imagemSelecionadaUri: Uri): String {
        return suspendCancellableCoroutine { continuation ->
            val numProcessoTratado =
                imagemSelecionadaUri.lastPathSegment!!.removeSpecialCharacters()

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "PROCESSO_${numProcessoTratado}_IMAGEM" + System.currentTimeMillis() + "."
                        + getFileExtension(imagemSelecionadaUri.lastPathSegment!!)
            )

            sRef.putFile(imagemSelecionadaUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            continuation.resume(imageUrl, null)
                        }
                        .addOnFailureListener { exception ->
                            continuation.cancel(exception)
                        }
                }
                .addOnFailureListener { exception ->
                    continuation.cancel(exception)
                }
        }
    }

    private fun validar(processo: Processo): List<String> {
        val erros = mutableListOf<String>()

        if (TextUtils.isEmpty(processo.titulo)) {
            erros.add("Campo 'Título' é obrigatório.")
        }

        if (TextUtils.isEmpty(processo.numero)) {
            erros.add("Campo 'Número do Processo' é obrigatório.")
        }

        if (TextUtils.isEmpty(processo.descricao)) {
            erros.add("Campo 'Descrição' é obrigatório.")
        }

        if (TextUtils.isEmpty(processo.tipo)) {
            erros.add("Campo 'Tipo' é obrigatório.")
        }

        if (TextUtils.isEmpty(processo.status)) {
            erros.add("Campo 'Status' é obrigatório.")
        }

        if (TextUtils.isEmpty(processo.dataInicio)) {
            erros.add("Campo 'Data de Início' é obrigatório.")
        }

        if (TextUtils.isEmpty(processo.cliente)) {
            erros.add("Campo 'Cliente' é obrigatório.")
        }

        if (TextUtils.isEmpty(processo.advogado)) {
            erros.add("Campo 'Advogado' é obrigatório.")
        }

        return erros
    }


    fun obterProcessos(
        onSuccessListener: (List<Processo>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessos(onSuccessListener, onFailureListener)
    }

    fun obterProcesso(
        id: String,
        onSuccessListener: (processo: Processo) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcesso(id, onSuccessListener, onFailureListener)
    }

    fun obterProcessoPorNumero(
        numero: String,
        onSuccessListener: (processo: Processo) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessoPorNumero(numero, onSuccessListener, onFailureListener)
    }

    fun obterProcessosByTituloContains(
        text: String,
        onSuccessListener: (lista: List<Processo>) -> Unit,
        onFailureListener: (ex: Exception?) -> Unit
    ) {
        repository.obterProcessosByTituloContains(text, onSuccessListener, onFailureListener)
    }

    suspend fun obterProcessos(): List<Processo>? {
        return repository.obterProcessos()
    }

    suspend fun obterProcesso(id: String): Processo? {
        return repository.obterProcesso(id)
    }

    private fun formatarAlteracoes(novoProcesso: Processo, atualProcesso: Processo): String {
        var retorno = "PROCESSO ATUALIZADO"

        if(!novoProcesso.status.equals(atualProcesso.status))
            retorno += "\nStatus: DE ${atualProcesso.statusObj!!.status} para ${novoProcesso.statusObj!!.status}"

        if(!novoProcesso.tipo.equals(atualProcesso.tipo))
            retorno += "\nTipo: DE ${atualProcesso.tipoObj!!.tipo} para ${novoProcesso.tipoObj!!.tipo}"

        if(!novoProcesso.advogado.equals(atualProcesso.advogado))
            retorno += "\nAdvogado: para ${novoProcesso.advogadoObj!!.nome}"

        if(!novoProcesso.cliente.equals(atualProcesso.cliente))
            retorno += "\nCliente: para ${novoProcesso.clienteObj!!.nome}"

        if(!novoProcesso.numero.equals(atualProcesso.numero))
            retorno += "\nNº Processo atualizado"

        if(!novoProcesso.titulo.equals(atualProcesso.titulo))
            retorno += "\nTítulo atualizado"

        if(!novoProcesso.descricao.equals(atualProcesso.descricao))
            retorno += "\nDescrição atualizada"

        if(novoProcesso.imagemSelecionadaURI != novoProcesso.imagemSelecionadaURI)
            retorno += "\nImagem atualizada"

        return retorno
    }
}