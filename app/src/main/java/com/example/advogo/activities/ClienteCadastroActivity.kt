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
import com.example.advogo.utils.constants.Constants
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher
import java.util.regex.Pattern
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

        binding.btnCep.setOnClickListener {
            binding.etEnderecoRua.isEnabled = false
            binding.etEnderecoCidade.isEnabled = false
            binding.etBairro.isEnabled = false
            binding.etEnderecoNumero.isEnabled = false

            var valor: String = binding.etCep.text.toString()

            if (valor.isNullOrEmpty()) {
                binding.etCep.error = "O campo não pode estar vazio"
                binding.etCep.requestFocus()
                return@setOnClickListener
            }

            val rgxCep: Pattern = Pattern.compile("(^\\d{5}-\\d{3}|^\\d{2}.\\d{3}-\\d{3}|\\d{8})")
            val matcher: Matcher = rgxCep.matcher(valor)

            if (!matcher.matches()) {
                binding.etCep.error = "Informe um CEP válido"
                binding.etCep.requestFocus()
            } else {
                valor = valor.replace("-", "")

                CoroutineScope(Dispatchers.Main).launch {
                    val endereco = buscarEnderecoCorreio(valor)

                    if(endereco != null) {
                        binding.etEnderecoRua.setText(endereco.logradouro)
                        binding.etEnderecoCidade.setText(endereco.localidade)
                        binding.etBairro.setText(endereco.bairro)

                        binding.etEnderecoRua.isEnabled = true
                        binding.etEnderecoCidade.isEnabled = true
                        binding.etBairro.isEnabled = true
                        binding.etEnderecoNumero.isEnabled = true
                    } else {
                        binding.etCep.error = "CEP não encontrado"
                        binding.etCep.requestFocus()
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveCliente() {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

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
            dataCriacao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            dataCriacaoTimestamp = Timestamp.now(),
            dataAlteracao = null,
            dataAlteracaoTimestamp = null
        )

        clienteRepository.adicionarCliente(
            cliente,
            { clienteCadastroSuccess() },
            { clienteCadastroFailure() }
        )
    }

    private fun clienteCadastroSuccess() {
        hideProgressDialog()

        intent.putExtra(Constants.FROM_CLIENTE_ACTIVITY, Constants.FROM_CLIENTE_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun clienteCadastroFailure() {
        hideProgressDialog()

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