package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.repositories.IProcessoRepository
import com.example.advogo.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoCadastroActivity : BaseActivity() {
    @Inject lateinit var _processoRepository: IProcessoRepository

    private lateinit var binding: ActivityProcessoCadastroBinding
    private lateinit var userName: String

    private var imagemSelecionadaURI: Uri? = null
    private var imagemSelecionadaURL: String? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessoCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        if (intent.hasExtra(Constants.ADV_NOME_PARAM)) {
            userName = intent.getStringExtra(Constants.ADV_NOME_PARAM)!!
        }

        binding.ivProcessoImage.setOnClickListener {
            chooseImage(this@ProcessoCadastroActivity, resultLauncher)
        }

        binding.btnProcessoCadastro.setOnClickListener {
            if(imagemSelecionadaURI != null) {
                salvarImagemProcesso()
            } else {
                saveProcesso()
            }
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagemSelecionadaURI = result.data!!.data!!

                try {
                    Glide
                        .with(this@ProcessoCadastroActivity)
                        .load(Uri.parse(imagemSelecionadaURI.toString()))
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(binding.ivProcessoImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProcessoCadastroActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Cadastro Processo"
        }

        binding.toolbarProcessoCadastroActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun salvarImagemProcesso() {
        //TODO("showProgressDialog("Please wait...")")

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "PROCESSO_IMAGE" + System.currentTimeMillis() + "."
                    + getFileExtension(imagemSelecionadaURI!!)
        )

        sRef.putFile(imagemSelecionadaURI!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        imagemSelecionadaURL = uri.toString()
                        saveProcesso()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@ProcessoCadastroActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                //TODO("hideProgressDialog()")
            }
    }

    private fun saveProcesso() {
        //TODO("showProgressDialog("Please wait...")")

        //TODO("preencher obj para add ou alterar")
        val processo = Processo(

        )

        _processoRepository.AdicionarProcesso(
            processo,
            { processoCadastroSuccess() },
            { processoCadastroFailure() }
        )
    }

    private fun processoCadastroSuccess() {
        //TODO("hideProgressDialog()")
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun processoCadastroFailure() {
        //TODO("hideProgressDialog()")

        Toast.makeText(
            this@ProcessoCadastroActivity,
            "Um erro ocorreu ao criar o processo.",
            Toast.LENGTH_SHORT
        ).show()
    }
}