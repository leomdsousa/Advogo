package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosTiposAdapter
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.Processo
import com.example.advogo.models.ProcessoStatus
import com.example.advogo.models.ProcessoTipo
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessoCadastroActivity : BaseActivity() {
    @Inject lateinit var processoRepository: IProcessoRepository
    @Inject lateinit var processoTipoRepository: IProcessoTipoRepository
    @Inject lateinit var processoStatusRepository: IProcessoStatusRepository

    private lateinit var binding: ActivityProcessoCadastroBinding
    private lateinit var userName: String

    private var advogados: List<Advogado> = ArrayList()
    private var processosTipos: List<ProcessoTipo> = ArrayList()
    private var processosStatus: List<ProcessoStatus> = ArrayList()
    private var dataSelecionada: String? = null

    private var imagemSelecionadaURI: Uri? = null
    private var imagemSelecionadaURL: String? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessoCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupSpinners()

        if (intent.hasExtra(Constants.ADV_NOME_PARAM)) {
            userName = intent.getStringExtra(Constants.ADV_NOME_PARAM)!!
        }

        binding.ivProcessoImage.setOnClickListener {
            chooseImage(this@ProcessoCadastroActivity, resultLauncher)
        }

        binding.etData.setOnClickListener {
            showDataPicker() { ano, mes, dia ->
                onDatePickerResult(ano, mes, dia)
            }
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

    private fun setupSpinners() {
        val spinnerTipos = findViewById<Spinner>(R.id.spinnerTipo)

        CoroutineScope(Dispatchers.Main).launch {
            val processosTiposDeferred = async { processoTipoRepository.ObterProcessosTipos() }
            processosTipos = processosTiposDeferred.await()!!

            val adapter = ProcessosTiposAdapter(this@ProcessoCadastroActivity, processosTipos)
            spinnerTipos.adapter = adapter
        }

        spinnerTipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as? String
                selectedItem?.let {
                    binding.etTipo.setText(it)
                    spinnerTipos.setSelection(id.toInt())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
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

        val processo = Processo(
            id = null,
            descricao = binding.etDescricao.text.toString(),
            numero = binding.etNumeroProcesso.text.toString(),
            tipo = binding.etTipo.text.toString(),
            status = binding.etStatus.text.toString(),
            data = dataSelecionada,
            imagem = imagemSelecionadaURL,
            cliente = binding.etCliente.text.toString(),
            advogado = binding.etAdv.text.toString(),
        )

        processoRepository.AdicionarProcesso(
            processo,
            { processoCadastroSuccess() },
            { processoCadastroFailure() }
        )
    }

    private fun onDatePickerResult(year: Int, month: Int, day: Int) {
        val sDayOfMonth = if (day < 10) "0$day" else "$day"
        val sMonthOfYear = if ((month + 1) < 10) "0${month + 1}" else "${month + 1}"

        dataSelecionada = "$sDayOfMonth/$sMonthOfYear/$year"
        binding.etData.setText(dataSelecionada)

//        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
//        val theDate = sdf.parse(selectedDate)
//        dataSelecionada = theDate!!.toLocaleString()
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