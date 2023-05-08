package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ActivityPerfilBinding
import com.example.advogo.models.Advogado
import com.example.advogo.repositories.IAdvogadoRepository
import com.google.common.base.Converter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class PerfilActivity : BaseActivity() {
    @Inject lateinit var _advRepository: IAdvogadoRepository
    private lateinit var binding: ActivityPerfilBinding
    private lateinit var advogadoDetalhes: Advogado
    private var imagemSelecionadaURI: Uri? = null
    private var imagemPerfilURL: String = ""

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        _advRepository.ObterAdvogado(
            getCurrentUserID(),
            { advogado -> setDadosPerfil(advogado) },
            { null }
        )

        binding.ivUserImage.setOnClickListener {
            chooseImage(this@PerfilActivity, resultLauncher)
        }

        binding.btnUpdate.setOnClickListener {
            if(imagemSelecionadaURI != null) {
                atualizarAdvogadoImagem()
            } else {
                //TODO("Exibir Progress Dialog")
                atualizarAdvogado()
                //TODO("Esconder Progress Dialog")
            }
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagemSelecionadaURI = result.data!!.data!!

                try {
                    Glide
                        .with(this@PerfilActivity)
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

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.profile)
        }

        binding.toolbarProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun atualizarAdvogado() {
        val advogado = Advogado(
            id = getCurrentUserID(),
            nome = (if (binding.etName.text.toString() != advogadoDetalhes.nome) binding.etName.text.toString() else advogadoDetalhes.nome),
            sobrenome = (if (binding.etSobrenome.text.toString() != advogadoDetalhes.sobrenome) binding.etSobrenome.text.toString() else advogadoDetalhes.sobrenome),
            email = (if (binding.etEmail.text.toString() != advogadoDetalhes.email) binding.etEmail.text.toString() else advogadoDetalhes.email),
            endereco = (if (binding.etEndereco.text.toString() != advogadoDetalhes.endereco) binding.etEndereco.text.toString() else advogadoDetalhes.endereco),
            enderecoLat = 0,
            enderecoLong = 0,
            imagem = (if (imagemPerfilURL.isNotEmpty() && imagemPerfilURL != advogadoDetalhes.imagem) imagemPerfilURL else advogadoDetalhes.imagem),
            oab = (if (binding.etOab.text.toString() != advogadoDetalhes.oab!!.toString()) binding.etOab.text.toString().toLong() else advogadoDetalhes.oab!!.toLong()),
            telefone = (if (binding.etTelefone.text.toString() != advogadoDetalhes.telefone) binding.etTelefone.text.toString() else advogadoDetalhes.telefone),
        )

        _advRepository.AtualizarAdvogado(
            advogado,
            { adv -> setDadosPerfil(advogado) },
            { null }
        )
    }

    private fun atualizarAdvogadoImagem() {
        //TODO("Exibir Progress Dialog")

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "ADVOGADO_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                imagemSelecionadaURI!!
            )
        )

        sRef.putFile(imagemSelecionadaURI!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        imagemPerfilURL = uri.toString()
                        atualizarAdvogado()
                    }

                //TODO("Esconder Progress Dialog")
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@PerfilActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                //TODO("Esconder Progress Dialog")
            }
    }

    fun setDadosPerfil(advogado: Advogado) {
        advogadoDetalhes = advogado

        Glide
            .with(this@PerfilActivity)
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

    fun atualizarPerfilSuccess() {
        //TODO("Exibir Progress Dialog")

        Toast.makeText(this@PerfilActivity,
            "Profile atualizado!",
            Toast.LENGTH_SHORT
        ).show()

        setResult(Activity.RESULT_OK)
        finish()
    }
}