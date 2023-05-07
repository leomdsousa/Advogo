package com.example.advogo.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.ActivityCadastroBinding
import com.example.advogo.models.Advogado
import com.example.advogo.repositories.IAdvogadoRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CadastroActivity : BaseActivity() {
    @Inject lateinit var _advRepository: IAdvogadoRepository
    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.btnSignUp.setOnClickListener { registrar() }
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
        val endereco = binding.etEndereco.text.toString().trim()
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
                            firebaseUser!!.uid,
                            nome,
                            firebaseUser.email!!,
                            endereco,
                            null,
                            oab,
                            telefone
                        )

                        _advRepository.AdicionarAdvogado(
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