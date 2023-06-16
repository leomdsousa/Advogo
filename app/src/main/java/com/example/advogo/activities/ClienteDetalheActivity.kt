package com.example.advogo.activities

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.advogo.R
import com.example.advogo.databinding.ActivityClienteDetalheBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.repositories.ClienteRepository
import com.example.advogo.services.CorreioApiService
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class ClienteDetalheActivity : BaseActivity() {
    private lateinit var binding: ActivityClienteDetalheBinding
    @Inject lateinit var clienteRepository: ClienteRepository
    @Inject lateinit var correioService: CorreioApiService

    private lateinit var clienteDetalhes: Cliente

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityClienteDetalheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        obterIntentDados()
        setupActionBar("Detalhe Cliente", binding.toolbarClienteDetalhe)
        setClienteToUI(clienteDetalhes)

        binding.btnAtualizarCliente.setOnClickListener {
            saveCliente()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cliente_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_deletar_cliente -> {
                alertDialogDeletarCliente(clienteDetalhes.nome!!)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun obterIntentDados() {
        if (intent.hasExtra(Constants.CLIENTE_PARAM)) {
            clienteDetalhes = intent.getParcelableExtra<Cliente>(Constants.CLIENTE_PARAM)!!
        }
    }

    private fun setClienteToUI(cliente: Cliente) {
        binding.etNome.setText(cliente.nome)
        binding.etCpf.setText(cliente.cpf)
        binding.etEmail.setText(cliente.email)
        binding.etEndereco.setText(cliente.endereco)
        cliente.enderecoNumero?.let { binding.etEnderecoNumero.setText(it) }
        binding.etEnderecoCidade.setText(cliente.enderecoCidade)
        binding.etBairro.setText(cliente.enderecoBairro)
        binding.etTelefone.setText(cliente.telefone)
    }

    private fun saveCliente() {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val cliente = Cliente(
            id = clienteDetalhes.id,
            nome = (if (binding.etNome.text.toString() != clienteDetalhes.nome) binding.etNome.text.toString() else clienteDetalhes.nome),
            cpf = (if (binding.etCpf.text.toString() != clienteDetalhes.cpf) binding.etCpf.text.toString() else clienteDetalhes.cpf),
            email = (if (binding.etEmail.text.toString() != clienteDetalhes.email) binding.etEmail.text.toString() else clienteDetalhes.email),
            endereco = (if (binding.etEndereco.text.toString() != clienteDetalhes.endereco) binding.etEndereco.text.toString() else clienteDetalhes.endereco),
            enderecoNumero = (if (binding.etEnderecoNumero.text.toString() != clienteDetalhes.enderecoNumero.toString()) binding.etEnderecoNumero.text.toString() else clienteDetalhes.enderecoNumero.toString()),
            enderecoBairro = (if (binding.etBairro.text.toString() != clienteDetalhes.enderecoBairro.toString()) binding.etBairro.text.toString() else clienteDetalhes.enderecoBairro.toString()),
            enderecoCidade = (if (binding.etEnderecoCidade.text.toString() != clienteDetalhes.enderecoCidade.toString()) binding.etEnderecoCidade.text.toString() else clienteDetalhes.enderecoCidade.toString()),
            telefone = (if (binding.etTelefone.text.toString() != clienteDetalhes.telefone) binding.etTelefone.text.toString() else clienteDetalhes.telefone),
        )

        clienteRepository.AtualizarCliente(
            cliente,
            { clienteEdicaoSuccess() },
            { clienteEdicaoFailure() }
        )
    }

    private fun deletarCliente() {
        clienteRepository.DeletarCliente(
            clienteDetalhes.id,
            { deletarClienteSuccess() },
            { deletarClienteFailure() }
        )
    }

    private fun alertDialogDeletarCliente(nome: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.atencao))
        builder.setMessage(
            resources.getString(
                R.string.confirmacaoDeletarCliente,
                clienteDetalhes.nome
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.sim)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deletarCliente()
        }

        builder.setNegativeButton(resources.getString(R.string.nao)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(binding.etNome.text.toString())) {
            binding.etNome.error = "Obrigatório"
            binding.etNome.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etCpf.text.toString())) {
            binding.etCpf.error = "Obrigatório"
            binding.etCpf.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEmail.text.toString())) {
            binding.etEmail.error = "Obrigatório"
            binding.etEmail.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etTelefone.text.toString())) {
            binding.etTelefone.error = "Obrigatório"
            binding.etTelefone.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEndereco.text.toString())) {
            binding.etEndereco.error = "Obrigatório"
            binding.etEndereco.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEnderecoNumero.text.toString())) {
            binding.etEnderecoNumero.error = "Obrigatório"
            binding.etEnderecoNumero.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etBairro.text.toString())) {
            binding.etBairro.error = "Obrigatório"
            binding.etBairro.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEnderecoCidade.text.toString())) {
            binding.etEnderecoCidade.error = "Obrigatório"
            binding.etEnderecoCidade.requestFocus()
            validado = false
        }

        return validado
    }

    private fun clienteEdicaoSuccess() {
        hideProgressDialog()

        intent.putExtra(Constants.FROM_CLIENTE_ACTIVITY, Constants.FROM_CLIENTE_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun clienteEdicaoFailure() {
        hideProgressDialog()

        Toast.makeText(
            this@ClienteDetalheActivity,
            "Um erro ocorreu ao atualizar o cliente.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private suspend fun buscarEnderecoCorreio(cep: String): CorreioResponse? {
        return correioService.obterEndereco(cep)
    }

    private fun deletarClienteSuccess() {
        hideProgressDialog()

        intent.putExtra(Constants.FROM_CLIENTE_ACTIVITY, Constants.FROM_CLIENTE_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun deletarClienteFailure() {
        hideProgressDialog()
    }
}