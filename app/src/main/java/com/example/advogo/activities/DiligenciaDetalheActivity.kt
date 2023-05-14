package com.example.advogo.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.advogo.R
import com.example.advogo.databinding.ActivityDiligenciaDetalheBinding
import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.repositories.DiligenciaRepository
import com.example.advogo.utils.Constants
import javax.inject.Inject

class DiligenciaDetalheActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaDetalheBinding
    @Inject lateinit var _diligenciaRepository: DiligenciaRepository

    private lateinit var diligenciaDetalhes: Diligencia

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiligenciaDetalheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        obterIntentDados()
        setupActionBar()

        setDiligenciaToUI(diligenciaDetalhes)

        binding.btnAtualizarDiligencia.setOnClickListener {
            saveDiligencia()
        }
    }

    private fun setDiligenciaToUI(diligencia: Diligencia) {
        binding.etDiligenciaDescricao.setText(diligencia.descricao)
        binding.etDiligenciaTipo.setText(diligencia.tipoObj?.tipo)
        binding.etDiligenciaStatus.setText(diligencia.statusObj?.status)
        binding.etDiligenciaData.setText(diligencia.data)
        binding.etDiligenciaProcesso.setText(diligencia.processoObj?.numero)
        binding.btnAtualizarDiligencia.text = diligencia.advogadoObj?.nome
        binding.etDiligenciaEndereco.setText(diligencia.endereco)
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

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarDiligenciaDetalhe)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Detalhe Diligência"
        }

        binding.toolbarDiligenciaDetalhe.setNavigationOnClickListener { onBackPressed() }
    }

    private fun obterIntentDados() {
        if (intent.hasExtra(Constants.DILIGENCIA_PARAM)) {
            diligenciaDetalhes = intent.getParcelableExtra<Diligencia>(Constants.DILIGENCIA_PARAM)!!
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_diligencia_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_deletar_diligencia -> {
                //alertDialogDeletarCliente(boardDetails.taskList!![taskListPosition].cards!![cardPosition].name)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun diligenciaCadastroSuccess() {
        //TODO("hideProgressDialog()")
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun diligenciaCadastroFailure() {
        //TODO("hideProgressDialog()")

        Toast.makeText(
            this@DiligenciaDetalheActivity,
            "Um erro ocorreu ao atualizar a diligência.",
            Toast.LENGTH_SHORT
        ).show()
    }
}