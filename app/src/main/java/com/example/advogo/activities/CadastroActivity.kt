package com.example.advogo.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.ActivityCadastroBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.services.CorreioApiService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class CadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityCadastroBinding
    @Inject lateinit var advRepository: IAdvogadoRepository
    @Inject lateinit var correioService: CorreioApiService

    private var endereco = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnSignUp.setOnClickListener { registrar() }

        binding.etCep.setOnClickListener {
            val valor: String = binding.etCep.text.toString()

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
                val endereco = buscarEnderecoCorreio(valor)

                if(endereco != null) {
                    binding.etCep.setText(endereco.logradouro)
                    binding.etEnderecoCidade.setText(endereco.localidade)
                    binding.etBairro.setText(endereco.bairro)
                } else {
                    binding.etCep.error = "CEP não encontrado"
                    binding.etCep.requestFocus()
                }
            }
        }
    }

    private fun buscarEnderecoCorreio(cep: String): CorreioResponse? {
        return correioService.obterEndereco(cep)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUp)
        val actionBar = supportActionBar

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.toolbarSignUp.setOnClickListener { onBackPressed() }
    }

    private fun registrar() {
        val nome = binding.etNome.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        //val endereco = binding.etEndereco.text.toString().trim()
        val endereco = endereco
        val oab = binding.etOab.text.toString().trim()
        val telefone = binding.etTelefone.text.toString().trim()

        if(validarFormulario(nome, email, endereco, oab, telefone, password)) {
            //TODO("Exibir Progress Dialog")

            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                        task ->
                    if(task.isSuccessful) {
                        val firebaseUser = task.result!!.user
                        val advogado = Advogado(
                            id = firebaseUser!!.uid,
                            nome = nome,
                            email = firebaseUser.email!!,
                            endereco = endereco,
                            enderecoLat = 0,
                            enderecoLong = 0,
                            imagem = null,
                            oab = oab.toLong(),
                            telefone = telefone
                        )

                        advRepository.AdicionarAdvogado(
                            advogado,
                            { registrarSuccess() },
                            { registrarFailure() }
                        )
                    } else {
                        Toast.makeText(
                            this@CadastroActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validarFormulario(
        nome: String,
        email: String,
        endereco: String,
        oab: String,
        telefone: String,
        password: String
    ): Boolean
    {
        return when {
            TextUtils.isEmpty(nome) -> {
                showErrorSnackBar("Por favor insira o seu nome")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Por favor insira o e-mail")
                false
            }
            TextUtils.isEmpty(endereco) -> {
                showErrorSnackBar("Por favor insira o seu endereço")
                false
            }
            TextUtils.isEmpty(oab) -> {
                showErrorSnackBar("Por favor insira a seu número OAB")
                false
            }
            TextUtils.isEmpty(telefone) -> {
                showErrorSnackBar("Por favor insira o seu telefone")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Por favor insira a senha")
                false
            } else -> {
                true
            }
        }
    }

    fun registrarSuccess() {
        //TODO("Fechar Progress Dialog")

        Toast.makeText(
            this@CadastroActivity,
            "Usuário registrado!",
            Toast.LENGTH_SHORT
        ).show()

        FirebaseAuth.getInstance().signOut()

        startActivity(Intent(this@CadastroActivity, MainActivity::class.java))
        finish()
    }

    fun registrarFailure() {
        //TODO("Fechar Progress Dialog")
    }
}