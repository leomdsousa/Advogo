package com.example.advogo.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.advogo.R
import com.example.advogo.databinding.ActivityCadastroBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.externals.CorreioResponse
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.services.CorreioApiService
import com.example.advogo.utils.constants.Constants
import com.example.advogo.utils.extensions.showPasswordVisibilityOnTouch
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class CadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityCadastroBinding
    @Inject lateinit var advRepository: IAdvogadoRepository
    @Inject lateinit var correioService: CorreioApiService

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Registre-se", binding.toolbarSignUp)

        binding.etTelefone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s.toString()

                if(value.isNotEmpty()) {
                    binding.tilChkWhatsapp.visibility = View.VISIBLE
                } else {
                    binding.tilChkWhatsapp.visibility = View.GONE
                    binding.chkWhatsapp.isChecked = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.btnSignUp.setOnClickListener { registrar() }

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

        binding.etPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                binding.etPassword.showPasswordVisibilityOnTouch(event)
            }
            false
        }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registrar() {
        val nome = binding.etNome.text.toString().trim()
        val sobrenome = binding.etSobrenome.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val endereco = binding.etEnderecoRua.text.toString()
        val enderecoNumero = binding.etEnderecoNumero.text.toString()
        val enderecoBairro = binding.etBairro.text.toString()
        val enderecoCidade = binding.etEnderecoCidade.text.toString()
        val oab = binding.etOab.text.toString().trim()
        val telefone = binding.etTelefone.text.toString().trim()
        val whatsapp = binding.chkWhatsapp.isChecked

        if(validarFormulario(
                nome,
                sobrenome,
                email,
                endereco,
                enderecoNumero,
                enderecoBairro,
                enderecoCidade,
                oab,
                telefone,
                password)
        ) {
            showProgressDialog(getString(R.string.aguardePorfavor))

            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                        task ->
                    if(task.isSuccessful) {
                        val firebaseUser = task.result!!.user
                        val advogado = Advogado(
                            id = firebaseUser!!.uid,
                            nome = nome,
                            sobrenome = sobrenome,
                            email = firebaseUser.email!!,
                            endereco = "$endereco, $enderecoNumero, $enderecoBairro, $enderecoCidade",
                            enderecoLat = 0.0,
                            enderecoLong = 0.0,
                            imagem = null,
                            oab = oab.toLong(),
                            telefone = telefone,
                            fcmToken = null,
                            dataCriacao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            dataCriacaoTimestamp = Timestamp.now(),
                            dataAlteracao = null,
                            dataAlteracaoTimestamp = null,
                            whatsapp = whatsapp
                        )

                        advRepository.adicionarAdvogado(
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
        sobrenome: String,
        email: String,
        endereco: String,
        enderecoNumero: String,
        enderecoBairro: String,
        enderecoCidade: String,
        oab: String,
        telefone: String,
        password: String
    ): Boolean
    {
            var validado = true

            if (TextUtils.isEmpty(nome)) {
                binding.etNome.error = "Por favor insira o seu nome"
                binding.etNome.requestFocus()
                validado = false
            }
            if (TextUtils.isEmpty(sobrenome)) {
                binding.etSobrenome.error = "Por favor insira o seu sobrenome"
                binding.etSobrenome.requestFocus()
                validado = false
            }
            if (TextUtils.isEmpty(email))  {
                binding.etEmail.error = "Por favor insira o e-mail"
                binding.etEmail.requestFocus()
                validado = false
            }
            if (TextUtils.isEmpty(endereco))  {
                binding.etEnderecoRua.error = "Por favor insira a rua"
                binding.etEnderecoRua.requestFocus()
                validado = false
            }
            if (TextUtils.isEmpty(enderecoNumero))  {
                binding.etEnderecoNumero.error = "Por favor insira o nº da rua"
                binding.etEnderecoNumero.requestFocus()
                validado = false
            }
            if (TextUtils.isEmpty(enderecoBairro))  {
                binding.etBairro.error = "Por favor insira o seu bairro"
                binding.etBairro.requestFocus()
                validado = false
            }
            if (TextUtils.isEmpty(enderecoCidade))  {
                binding.etEnderecoRua.error = "Por favor insira a sua cidade"
                binding.etEnderecoRua.requestFocus()
                validado = false
            }
            if (TextUtils.isEmpty(oab)) {
                binding.etOab.error = "Por favor insira a seu número OAB"
                binding.etOab.requestFocus()
                validado = false
            }
            if (TextUtils.isEmpty(telefone)) {
                binding.etTelefone.error = "Por favor insira o seu telefone"
                binding.etTelefone.requestFocus();
                validado = false
            }
            if (TextUtils.isEmpty(password)) {
                binding.etPassword.error = "Por favor insira a senha"
                binding.etPassword.requestFocus();
                validado = false
            }

            return validado
    }

    private fun registrarSuccess() {
        hideProgressDialog()

        var intent = Intent(this@CadastroActivity, MainActivity::class.java)
        intent.putExtra(Constants.FROM_REGISTRAR_ACTIVITY, Constants.FROM_REGISTRAR_ACTIVITY)

        startActivity(intent)
        finish()
    }

    private fun registrarFailure() {
        hideProgressDialog()
    }
}