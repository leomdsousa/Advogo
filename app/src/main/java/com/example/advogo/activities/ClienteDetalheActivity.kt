package com.example.advogo.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.ActivityClienteCadastroBinding
import com.example.advogo.databinding.ActivityClienteDetalheBinding
import com.example.advogo.models.Cliente
import com.example.advogo.models.Processo
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.repositories.ClienteRepository
import com.example.advogo.services.CorreioApiService
import com.example.advogo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClienteDetalheActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClienteDetalheBinding
    @Inject lateinit var _clienteRepository: ClienteRepository
    @Inject lateinit var correioService: CorreioApiService

    private lateinit var clienteDetalhes: Cliente

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityClienteDetalheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        obterIntentDados()
        setupActionBar()

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
            R.id.action_deletar_cliente -> {
                //alertDialogDeletarCliente(boardDetails.taskList!![taskListPosition].cards!![cardPosition].name)
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

    private fun saveCliente() {
        //TODO("showProgressDialog("Please wait...")")

        //TODO("preencher obj para add ou alterar")
        val cliente = Cliente(

        )

        _clienteRepository.AtualizarCliente(
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
            this@ClienteDetalheActivity,
            "Um erro ocorreu ao atualizar o cliente.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun BuscarEnderecoCorreio(cep: String): CorreioResponse? {
        return correioService.obterEndereco(cep)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarClienteDetalhe)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Detalhe Cliente"
        }

        binding.toolbarClienteDetalhe.setNavigationOnClickListener { onBackPressed() }
    }
}