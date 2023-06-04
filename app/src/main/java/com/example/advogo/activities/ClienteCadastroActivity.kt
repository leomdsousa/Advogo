package com.example.advogo.activities

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.advogo.R
import com.example.advogo.databinding.ActivityClienteCadastroBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.repositories.ClienteRepository
import com.example.advogo.services.CorreioApiService
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClienteCadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityClienteCadastroBinding
    @Inject lateinit var clienteRepository: ClienteRepository
    @Inject lateinit var correioService: CorreioApiService

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityClienteCadastroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupActionBar("Cadastro Cliente", binding.toolbarClienteCadastro)

        binding.btnCadastroCliente.setOnClickListener {
            saveCliente()
        }
    }

    private fun saveCliente() {
        if(!validarFormulario()) {
            return
        }

        //TODO("showProgressDialog("Please wait...")")

        val cliente = Cliente(
            id = "",
            nome = binding.etNome.text.toString(),
            cpf = binding.etCpf.text.toString(),
            email = binding.etEmail.text.toString(),
            endereco = binding.etEnderecoRua.text.toString(),
            enderecoNumero = binding.etEnderecoNumero.text.toString(),
            enderecoCidade = binding.etEnderecoCidade.text.toString(),
            enderecoBairro = binding.etBairro.text.toString(),
            telefone = binding.etTelefone.text.toString(),
        )

        clienteRepository.AdicionarCliente(
            cliente,
            { clienteCadastroSuccess() },
            { clienteCadastroFailure() }
        )
    }

    private fun clienteCadastroSuccess() {
        //TODO("hideProgressDialog()")
        intent.putExtra(Constants.FROM_CLIENTE_ACTIVITY, Constants.FROM_CLIENTE_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun clienteCadastroFailure() {
        //TODO("hideProgressDialog()")

        Toast.makeText(
            this@ClienteCadastroActivity,
            "Um erro ocorreu ao criar o cliente.",
            Toast.LENGTH_SHORT
        ).show()
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

        if (TextUtils.isEmpty(binding.etEnderecoRua.text.toString())) {
            binding.etEnderecoRua.error = "Obrigatório"
            binding.etEnderecoRua.requestFocus()
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

    private suspend fun buscarEnderecoCorreio(cep: String): CorreioResponse? {
        return correioService.obterEndereco(cep)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}