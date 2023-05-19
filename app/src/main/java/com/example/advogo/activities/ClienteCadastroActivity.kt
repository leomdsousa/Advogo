package com.example.advogo.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.advogo.R
import com.example.advogo.databinding.ActivityClienteCadastroBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.repositories.ClienteRepository
import com.example.advogo.services.CorreioApiService
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class ClienteCadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityClienteCadastroBinding
    @Inject lateinit var clienteRepository: ClienteRepository
    @Inject lateinit var correioService: CorreioApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityClienteCadastroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupActionBar()

        binding.btnCadastroCliente.setOnClickListener {
            saveCliente()
        }

//        binding.etCep.setOnClickListener {
//            val valor: String = binding.etCep.text.toString()
//
//            if (valor.isNullOrEmpty()) {
//                binding.etCep.error = "O campo não pode estar vazio"
//                binding.etCep.requestFocus()
//                return@setOnClickListener
//            }
//
//            val rgxCep: Pattern = Pattern.compile("(^\\d{5}-\\d{3}|^\\d{2}.\\d{3}-\\d{3}|\\d{8})")
//            val matcher: Matcher = rgxCep.matcher(valor)
//
//            if (!matcher.matches()) {
//                binding.etCep.error = "Informe um CEP válido"
//                binding.etCep.requestFocus()
//            } else {
//                val endereco = buscarEnderecoCorreio(valor)
//
//                if(endereco != null) {
//                    binding.etCep.setText(endereco.logradouro)
//                    binding.etEnderecoCidade.setText(endereco.localidade)
//                    binding.etBairro.setText(endereco.bairro)
//                } else {
//                    binding.etCep.error = "CEP não encontrado"
//                    binding.etCep.requestFocus()
//                }
//            }
//        }
    }

    private fun saveCliente() {
        //TODO("showProgressDialog("Please wait...")")

        val cliente = Cliente(
            id = null,
            nome = binding.etNome.text.toString(),
            cpf = binding.etCpf.text.toString(),
            email = binding.etEmail.text.toString(),
            //endereco = binding.etEndereco.text.toString(),
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
        setResult(Activity.RESULT_OK)
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

    private fun buscarEnderecoCorreio(cep: String): CorreioResponse? {
        return correioService.obterEndereco(cep)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarClienteCadastro)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Cadastro Cliente"
        }

        binding.toolbarClienteCadastro.setNavigationOnClickListener { onBackPressed() }
    }
}