package com.example.advogo.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.ActivityDiligenciaCadastroBinding
import com.example.advogo.models.Diligencia
import com.example.advogo.repositories.DiligenciaRepository
import javax.inject.Inject

class DiligenciaCadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaCadastroBinding
    @Inject lateinit var _diligenciaRepository: DiligenciaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiligenciaCadastroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupActionBar()

        binding.btnCadastrarDiligencia.setOnClickListener {
            saveDiligencia()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarDiligenciaCadastro)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Cadastro Diligência"
        }

        binding.toolbarDiligenciaCadastro.setNavigationOnClickListener { onBackPressed() }
    }

    private fun saveDiligencia() {
        //TODO("showProgressDialog("Please wait...")")

        //TODO("preencher obj para add ou alterar")
        val diligencia = Diligencia(

        )

        _diligenciaRepository.AdicionarDiligencia(
            diligencia,
            { diligenciaCadastroSuccess() },
            { diligenciaCadastroFailure() }
        )
    }

    private fun diligenciaCadastroSuccess() {
        //TODO("hideProgressDialog()")
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun diligenciaCadastroFailure() {
        //TODO("hideProgressDialog()")

        Toast.makeText(
            this@DiligenciaCadastroActivity,
            "Um erro ocorreu ao criar a diligência.",
            Toast.LENGTH_SHORT
        ).show()
    }
}