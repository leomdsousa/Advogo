package com.example.advogo.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.ActivityClienteCadastroBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.Processo
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.repositories.ClienteRepository
import com.example.advogo.services.CorreioApiService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClienteCadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClienteCadastroBinding
    @Inject lateinit var _clienteRepository: ClienteRepository
    @Inject lateinit var correioService: CorreioApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityClienteCadastroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupActionBar()

        binding.btnCadastroCliente.setOnClickListener {
            saveCliente()
        }
    }

    private fun saveCliente() {
        //TODO("showProgressDialog("Please wait...")")

        //TODO("preencher obj para add ou alterar")
        val cliente = Cliente(

        )

        _clienteRepository.AdicionarCliente(
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

    private fun BuscarEnderecoCorreio(cep: String): CorreioResponse? {
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