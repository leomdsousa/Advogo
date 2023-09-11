package com.example.advogo.services

import android.os.Build
import android.text.TextUtils
import androidx.annotation.RequiresApi
import com.example.advogo.models.Cliente
import com.example.advogo.repositories.IClienteRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ClientesService {
    @Inject lateinit var repository: IClienteRepository

    @RequiresApi(Build.VERSION_CODES.O)
    fun cadastrar(
        cliente: Cliente,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(cliente)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        repository.adicionarCliente(
            cliente,
            {
                onSuccess()
            },
            { onFailure(listOf("Falha ao salvar cliente.")) }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun atualizar(
        cliente: Cliente,
        onSuccess: () -> Unit,
        onFailure: (List<String>) -> Unit
    ) {
        val erros = validar(cliente)

        if (erros.isNotEmpty()) {
            onFailure(erros)
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val clienteDetalhesDeferred = async { repository.obterCliente(cliente.id) }
            val clienteAtual = clienteDetalhesDeferred.await()!!

            val input = Cliente(
                id = clienteAtual.id,
                nome = cliente.nome.takeIf { it!!.isNotEmpty() } ?: clienteAtual.nome,
                cpf = cliente.cpf.takeIf { it!!.isNotEmpty() } ?: clienteAtual.cpf,
                email = cliente.email.takeIf { it!!.isNotEmpty() } ?: clienteAtual.email,
                endereco = cliente.endereco.takeIf { it!!.isNotEmpty() } ?: clienteAtual.endereco,
                enderecoNumero = cliente.enderecoNumero.takeIf { it!!.isNotEmpty() } ?: clienteAtual.enderecoNumero,
                enderecoBairro = cliente.enderecoBairro.takeIf { it!!.isNotEmpty() } ?: clienteAtual.enderecoBairro,
                enderecoCidade = cliente.enderecoCidade.takeIf { it!!.isNotEmpty() } ?: clienteAtual.enderecoCidade,
                telefone = cliente.telefone.takeIf { it!!.isNotEmpty() } ?: clienteAtual.telefone,
                dataCriacao = clienteAtual.dataCriacao,
                dataCriacaoTimestamp = clienteAtual.dataCriacaoTimestamp,
                dataAlteracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                dataAlteracaoTimestamp = Timestamp.now()
            )

            repository.atualizarCliente(
                input,
                {
                    onSuccess()
                },
                { onFailure(listOf("Falha ao salvar cliente.")) }
            )
        }
    }

    fun deletar(
        clienteId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        repository.deletarCliente(
            clienteId,
            { onSuccess() },
            { onFailure() }
        )
    }

    private fun validar(cliente: Cliente): List<String> {
        val erros = mutableListOf<String>()

        if (TextUtils.isEmpty(cliente.nome)) {
            erros.add("Campo 'Nome' é obrigatório.")
        }

        if (TextUtils.isEmpty(cliente.cpf)) {
            erros.add("Campo 'CPF' é obrigatório.")
        }

        if (TextUtils.isEmpty(cliente.email)) {
            erros.add("Campo 'Email' é obrigatório.")
        }

        if (TextUtils.isEmpty(cliente.telefone)) {
            erros.add("Campo 'Telefone' é obrigatório.")
        }

        if (TextUtils.isEmpty(cliente.endereco)) {
            erros.add("Campo 'Endereço' é obrigatório.")
        }

        if (TextUtils.isEmpty(cliente.enderecoNumero)) {
            erros.add("Campo 'Número do Endereço' é obrigatório.")
        }

        if (TextUtils.isEmpty(cliente.enderecoBairro)) {
            erros.add("Campo 'Bairro' é obrigatório.")
        }

        if (TextUtils.isEmpty(cliente.enderecoCidade)) {
            erros.add("Campo 'Cidade' é obrigatório.")
        }

        return erros
    }

    fun obterClientes(
        onSuccessListener: (lista: List<Cliente>) -> Unit,
        onFailureListener: (exception: Exception?) -> Unit
    ) {
        repository.obterClientes(onSuccessListener, onFailureListener)
    }

    fun obterCliente(
        id: String,
        onSuccessListener: (cliente: Cliente) -> Unit,
        onFailureListener: (exception: Exception?) -> Unit
    ) {
        repository.obterCliente(id, onSuccessListener, onFailureListener)
    }

    fun obterClientesByNomeContains(
        text: String,
        onSuccessListener: (lista: List<Cliente>) -> Unit,
        onFailureListener: (exception: Exception?) -> Unit
    ) {
        repository.obterClientesByNomeContains(text, onSuccessListener, onFailureListener)
    }

    fun obterClientePorEmail(
        email: String,
        onSuccessListener: (cliente: Cliente) -> Unit,
        onFailureListener: (exception: Exception?) -> Unit
    ) {
        repository.obterClientePorEmail(email, onSuccessListener, onFailureListener)
    }


    suspend fun obterClientes(): List<Cliente>? {
        return repository.obterClientes()
    }

    suspend fun obterCliente(id: String): Cliente? {
        return repository.obterCliente(id)
    }
}