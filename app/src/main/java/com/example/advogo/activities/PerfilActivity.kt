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
//        val userHashMap = HashMap<String, Any>()
//
//        if (profileImagemURL.isNotEmpty() && profileImagemURL != userDetails.image) {
//            userHashMap[Constants.USER_IMAGE] = profileImagemURL
//        }
//
//        if (binding.etName.text.toString() != userDetails.name) {
//            userHashMap[Constants.USER_NAME] = binding.etName.text.toString()
//        }
//
//        if (binding.etMobile.text.toString() != userDetails.mobile.toString()) {
//            userHashMap[Constants.USER_MOBILE] = binding.etMobile.text.toString().toLong()
//        }

        val advogado = Advogado(
            getCurrentUserID(),
            (if (binding.etName.text.toString() != advogadoDetalhes.nome) binding.etName.text.toString() else advogadoDetalhes.nome),
            (if (binding.etEmail.text.toString() != advogadoDetalhes.email) binding.etEmail.text.toString() else advogadoDetalhes.email),
            (if (binding.etEndereco.text.toString() != advogadoDetalhes.endereco) binding.etEndereco.text.toString() else advogadoDetalhes.endereco),
            (if (imagemPerfilURL.isNotEmpty() && imagemPerfilURL != advogadoDetalhes.image) imagemPerfilURL else advogadoDetalhes.image),
            (if (binding.etOab.text.toString() != advogadoDetalhes.oab) binding.etOab.text.toString() else advogadoDetalhes.oab),
            (if (binding.etTelefone.text.toString() != advogadoDetalhes.telefone) binding.etTelefone.text.toString() else advogadoDetalhes.telefone),
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
            .load(advogadoDetalhes.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivUserImage)

        binding.etName.setText(advogadoDetalhes.nome)
        binding.etEmail.setText(advogadoDetalhes.email)
        binding.etOab.setText(advogadoDetalhes.oab)
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