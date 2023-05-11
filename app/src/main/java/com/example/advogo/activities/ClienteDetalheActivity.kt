package com.example.advogo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.advogo.R
import com.example.advogo.databinding.ActivityClienteDetalheBinding
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.services.CorreioApiService
import javax.inject.Inject

class ClienteDetalheActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClienteDetalheBinding
    @Inject lateinit var correioService: CorreioApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cliente_detalhe)
    }

    private fun BuscarEnderecoCorreio(cep: String): CorreioResponse? {
        return correioService.obterEndereco(cep)
    }
}