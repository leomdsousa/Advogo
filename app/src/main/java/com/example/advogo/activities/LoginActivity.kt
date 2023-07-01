package com.example.advogo.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.advogo.R
import com.example.advogo.databinding.ActivityLoginBinding
import com.example.advogo.models.Advogado
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.utils.Constants
import com.example.advogo.utils.extensions.showPasswordVisibilityOnTouch
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding

    @Inject lateinit var advRepository: IAdvogadoRepository
    private val firebaseAuth = FirebaseAuth.getInstance()

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Login", binding.toolbarSignIn)
        binding.btnLogin.setOnClickListener { logar() }

        binding.etPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                binding.etPassword.showPasswordVisibilityOnTouch(event)
            }
            false
        }
    }

    private fun logar() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if(validarFormulario()) {
            showProgressDialog(getString(R.string.aguardePorfavor))

            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        advRepository.obterAdvogado(
                            getCurrentUserID(),
                            { advogado -> loginSuccess() },
                            { loginFailure() }
                        )
                    } else {
                        loginFailure()

                        Toast.makeText(
                            this@LoginActivity,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(binding.etEmail.text.toString())) {
            binding.etEmail.error = "Obrigatório"
            binding.etEmail.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etPassword.text.toString())) {
            binding.etPassword.error = "Obrigatório"
            binding.etPassword.requestFocus()
            validado = false
        }

        return validado
    }

    private fun loginSuccess() {
        hideProgressDialog()

        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra(Constants.FROM_LOGIN_ACTIVITY, Constants.FROM_LOGIN_ACTIVITY)
        startActivity(intent)
        finish()
    }

    private fun loginFailure() {
        hideProgressDialog()
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