package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ActivityAdvogadoDetalheBinding
import com.example.advogo.models.Advogado
import com.example.advogo.repositories.AdvogadoRepository
import com.example.advogo.services.CorreioApiService
import com.example.advogo.utils.constants.Constants
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class AdvogadoDetalheActivity : BaseActivity() {
    private lateinit var binding: ActivityAdvogadoDetalheBinding
    @Inject lateinit var advogadoRepository: AdvogadoRepository
    @Inject lateinit var correioService: CorreioApiService

    private lateinit var advogadoDetalhes: Advogado

    private var imagemSelecionadaURI: Uri? = null
    private var imagemPerfilURL: String = ""

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAdvogadoDetalheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        obterIntentDados()
        setupActionBar("Detalhe Advogado", binding.toolbarAdvogadoDetalhe)
        setAdvogadoToUI(advogadoDetalhes)

        binding.ivUserImage.setOnClickListener {
            chooseImage(this@AdvogadoDetalheActivity, resultLauncher)
        }

        binding.btnUpdate.setOnClickListener {
            saveAdvogado()
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagemSelecionadaURI = result.data!!.data!!

                try {
                    Glide
                        .with(this@AdvogadoDetalheActivity)
                        .load(Uri.parse(imagemSelecionadaURI.toString()))
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(binding.ivUserImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_advogados_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_deletar_advogado -> {
                alertDialogDeletarAdvogado("${advogadoDetalhes.nome!!} (${advogadoDetalhes.oab!!})")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun obterIntentDados() {
        if (intent.hasExtra(Constants.ADV_PARAM)) {
            advogadoDetalhes = intent.getParcelableExtra<Advogado>(Constants.ADV_PARAM)!!
        }
    }

    private fun setAdvogadoToUI(advogado: Advogado) {
        advogadoDetalhes = advogado

        Glide
            .with(this@AdvogadoDetalheActivity)
            .load(advogadoDetalhes.imagem)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivUserImage)

        binding.etName.setText(advogadoDetalhes.nome)
        binding.etSobrenome.setText(advogadoDetalhes.sobrenome)
        binding.etEmail.setText(advogadoDetalhes.email)
        binding.etOab.setText(advogadoDetalhes.oab.toString())
        binding.etEndereco.setText(advogadoDetalhes.endereco)
        binding.etTelefone.setText(advogadoDetalhes.telefone)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveAdvogado() {
        if (!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val imageUrl = if (imagemSelecionadaURI != null) {
                atualizarAdvogadoImagem()
            } else {
                advogadoDetalhes.imagem
            }

            val advogado = Advogado(
                id = advogadoDetalhes.id,
                nome = (if (binding.etName.text.toString() != advogadoDetalhes.nome) binding.etName.text.toString() else advogadoDetalhes.nome),
                sobrenome = (if (binding.etSobrenome.text.toString() != advogadoDetalhes.sobrenome) binding.etSobrenome.text.toString() else advogadoDetalhes.sobrenome),
                email = (if (binding.etEmail.text.toString() != advogadoDetalhes.email) binding.etEmail.text.toString() else advogadoDetalhes.email),
                endereco = (if (binding.etEndereco.text.toString() != advogadoDetalhes.endereco) binding.etEndereco.text.toString() else advogadoDetalhes.endereco),
                enderecoLat = advogadoDetalhes.enderecoLat,
                enderecoLong = advogadoDetalhes.enderecoLong,
                oab = (if (binding.etOab.text.toString() != advogadoDetalhes.oab!!.toString()) binding.etOab.text.toString().toLong() else advogadoDetalhes.oab!!.toLong()),
                telefone = (if (binding.etTelefone.text.toString() != advogadoDetalhes.telefone) binding.etTelefone.text.toString() else advogadoDetalhes.telefone),
                dataCriacao = advogadoDetalhes.dataCriacao,
                dataCriacaoTimestamp = advogadoDetalhes.dataCriacaoTimestamp,
                dataAlteracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                dataAlteracaoTimestamp = Timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).fromUSADateStringToDate()),
                imagem = imageUrl,
                fcmToken = advogadoDetalhes.fcmToken,
            )

            try {
                advogadoRepository.atualizarAdvogado(
                    advogado,
                    {
                        setAdvogadoToUI(advogado)
                        advogadoEdicaoSuccess()
                    },
                    { advogadoEdicaoFailure() }
                )
            } catch (e: Exception) {
                advogadoEdicaoFailure()
            }
        }
    }

    private suspend fun atualizarAdvogadoImagem(): String {
        return suspendCancellableCoroutine { continuation ->
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "ADVOGADO_${advogadoDetalhes.oab}_IMAGEM" + System.currentTimeMillis() + "." + getFileExtension(
                    imagemSelecionadaURI!!
                )
            )

            sRef.putFile(imagemSelecionadaURI!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            continuation.resume(imageUrl, null)
                        }
                        .addOnFailureListener { exception ->
                            continuation.cancel(exception)
                        }
                }
                .addOnFailureListener { exception ->
                    continuation.cancel(exception)
                }
        }
    }

    private fun deletarAdvogado() {
        advogadoRepository.deletarAdvogado(
            advogadoDetalhes.id,
            { deletarAdvogadoSuccess() },
            { deletarAdvogadoFailure() }
        )
    }

    private fun alertDialogDeletarAdvogado(nome: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.atencao))
        builder.setMessage(
            resources.getString(
                R.string.confirmacaoDeletarAdvogado,
                advogadoDetalhes.nome
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.sim)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deletarAdvogado()
        }

        builder.setNegativeButton(resources.getString(R.string.nao)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(binding.etName.text.toString())) {
            binding.etName.error = "Obrigatório"
            binding.etName.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etSobrenome.text.toString())) {
            binding.etSobrenome.error = "Obrigatório"
            binding.etSobrenome.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEmail.text.toString())) {
            binding.etEmail.error = "Obrigatório"
            binding.etEmail.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etTelefone.text.toString())) {
            binding.etTelefone.error = "Obrigatório"
            binding.etTelefone.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etOab.text.toString())) {
            binding.etOab.error = "Obrigatório"
            binding.etOab.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etEndereco.text.toString())) {
            binding.etEndereco.error = "Obrigatório"
            binding.etEndereco.requestFocus()
            validado = false
        }

        return validado
    }

    private fun advogadoEdicaoSuccess() {
        hideProgressDialog()

        intent.putExtra(Constants.FROM_ADVOGADO_ACTIVITY, Constants.FROM_ADVOGADO_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun advogadoEdicaoFailure() {
        hideProgressDialog()

        Toast.makeText(
            this@AdvogadoDetalheActivity,
            "Um erro ocorreu ao atualizar o advogado.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deletarAdvogadoSuccess() {
        hideProgressDialog()

        intent.putExtra(Constants.FROM_ADVOGADO_ACTIVITY, Constants.FROM_ADVOGADO_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun deletarAdvogadoFailure() {
        hideProgressDialog()
    }
}