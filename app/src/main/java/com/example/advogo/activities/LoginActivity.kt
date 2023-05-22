package com.example.advogo.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.ActivityLoginBinding
import com.example.advogo.models.Advogado
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    @Inject lateinit var advRepository: IAdvogadoRepository
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        binding.btnLogin.setOnClickListener { logar() }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignIn)
        val actionBar = supportActionBar

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.toolbarSignIn.setOnClickListener { onBackPressed() }
    }

    private fun logar() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if(validarFormulario(email, password)) {
            //TODO("Mostrar progress bar")

            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        advRepository.ObterAdvogado(
                            getCurrentUserID(),
                            { advogado -> loginSuccess(advogado) },
                            { loginFailure() }
                        )
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validarFormulario(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Por favor insira o e-mail")
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

    fun loginSuccess(advogado: Advogado) {
        //TODO("Fechar progress bar")
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra(Constants.FROM_LOGIN_ACTIVITY, Constants.FROM_LOGIN_ACTIVITY)
        startActivity(intent)
        finish()
    }

    fun loginFailure() {
        //TODO("Fechar progress bar")
    }
}