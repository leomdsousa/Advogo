package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.advogo.R
import com.example.advogo.adapters.DiligenciasStatusAdapter
import com.example.advogo.adapters.DiligenciasTiposAdapter
import com.example.advogo.databinding.ActivityDiligenciaDetalheBinding
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class DiligenciaDetalheActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaDetalheBinding
    @Inject lateinit var diligenciaRepository: DiligenciaRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository
    @Inject lateinit var clienteRepository: ClienteRepository
    @Inject lateinit var diligenciaTipoRepository: DiligenciaTipoRepository
    @Inject lateinit var diligenciaStatusRepository: DiligenciaStatusRepository
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var diligenciaDetalhes: Diligencia
    private var savedLatitude: Double = 0.0
    private var savedLongitude: Double = 0.0
    private var dataSelecionada: String? = null

    private var diligenciaStatus: List<DiligenciaStatus>? = ArrayList()
    private var diligenciaTipos: List<DiligenciaTipo>? = ArrayList()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiligenciaDetalheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        obterIntentDados()
        setupActionBar()
        configurarGoogleMapPlaces()
        setupSpinners()

        setDiligenciaToUI(diligenciaDetalhes)

        binding.btnAtualizarDiligencia.setOnClickListener {
            saveDiligencia()
        }

        binding.etDiligenciaEndereco.setOnClickListener {
            showGoogleMapPlaces(this, resultLauncher)
        }

        binding.etDiligenciaData.setOnClickListener {
            showDataPicker() { ano, mes, dia ->
                onDatePickerResult(ano, mes, dia)
            }
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
//                    if(result.data!!.hasExtra(Constants.FROM_DEVICE_GALLERY)) {
//                        val contentUri = result.data!!.data
//                        var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
//                        savedUriImage = saveImageToInternalStorage(bitmap)
//                        Log.e("Info", savedUriImage?.path.toString())
//                        binding.ivPlace.setImageURI(contentUri)
//                    } else if(result.data!!.hasExtra(Constants.FROM_DEVICE_CAMERA)) {
//                        val contentBitmap: Bitmap = data.extras!!.get("data") as Bitmap
//                        savedUriImage = saveImageToInternalStorage(contentBitmap)
//                        Log.e("Info", savedUriImage?.path.toString())
//                        binding.ivPlace.setImageBitmap(contentBitmap)
//                    }
                    if(result.data!!.hasExtra(Constants.FROM_GOOGLE_PLACES)) {
                        val place: Place = Autocomplete.getPlaceFromIntent(result.data!!)
                        binding.etDiligenciaEndereco.setText(place.address)
                        savedLatitude = place.latLng!!.latitude
                        savedLongitude = place.latLng!!.longitude
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupSpinners() {
        setupSpinnerTipoDiligencia()
        setupSpinnerStatusDiligencia()
    }

    private fun setupSpinnerStatusDiligencia() {
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatusDiligencia)

        CoroutineScope(Dispatchers.Main).launch {
            val diligenciaStatusDeferred = async { diligenciaStatusRepository.ObterDiligenciasStatus() }
            diligenciaStatus = diligenciaStatusDeferred.await()!!

            val adapter = DiligenciasStatusAdapter(this@DiligenciaDetalheActivity, diligenciaStatus!!
            )
            spinnerStatus.adapter = adapter
        }

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as? String
                selectedItem?.let {
                    binding.autoTvStatusDiligencia.setText(it)
                    spinnerStatus.setSelection(id.toInt())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }
    }

    private fun setupSpinnerTipoDiligencia() {
        val spinnerTipos = findViewById<Spinner>(R.id.spinnerTipoDiligencia)

        CoroutineScope(Dispatchers.Main).launch {
            val diligenciaTiposDeferred = async { diligenciaTipoRepository.ObterDiligenciasTipos() }
            diligenciaTipos = diligenciaTiposDeferred.await()!!

            val adapter = DiligenciasTiposAdapter(this@DiligenciaDetalheActivity, diligenciaTipos!!)
            spinnerTipos.adapter = adapter
        }

        spinnerTipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as? String
                selectedItem?.let {
                    binding.autoTvTipoDiligencia.setText(it)
                    spinnerTipos.setSelection(id.toInt())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }
    }

    private fun setDiligenciaToUI(diligencia: Diligencia) {
        binding.etDiligenciaDescricao.setText(diligencia.descricao)
        binding.autoTvTipoDiligencia.setText(diligencia.tipoObj?.tipo)
        binding.autoTvStatusDiligencia.setText(diligencia.statusObj?.status)
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

        diligenciaRepository.AdicionarDiligencia(
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
                alertDialogDeletarDiligencia("${diligenciaDetalhes.descricao!!} (Processo ${diligenciaDetalhes.processo})")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun alertDialogDeletarDiligencia(texto: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.atencao))
        builder.setMessage(
            resources.getString(
                R.string.confirmacaoDeletarDiligencia,
                texto
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.sim)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deletarDiligencia()
        }

        builder.setNegativeButton(resources.getString(R.string.nao)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deletarDiligencia() {
        diligenciaRepository.DeletarDiligencia(
            diligenciaDetalhes.id!!,
            { deletarDiligenciaSuccess() },
            { deletarDiligenciaFailure() }
        )
    }

    private fun carregarAdvogados(): List<Advogado> {
        var retorno: List<Advogado> = ArrayList()

        advogadoRepository.ObterAdvogados(
            { lista -> retorno = lista },
            { null } //TODO("Implementar")
        )

        return retorno
    }

    private fun carregarClientes(): List<Cliente> {
        var retorno: List<Cliente> = ArrayList()

        clienteRepository.ObterClientes(
            { lista -> retorno = lista },
            { null } //TODO("Implementar")
        )

        return retorno
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

    private fun deletarDiligenciaSuccess() {
        //TODO("hideProgressDialog()")
        finish()
    }

    private fun deletarDiligenciaFailure() {
        //TODO("hideProgressDialog()")
    }

    private fun configurarGoogleMapPlaces() {
        if(!Places.isInitialized()) {
            Places.initialize(this@DiligenciaDetalheActivity,
                resources.getString(R.string.google_maps_api_key))
        }
    }

    private fun onDatePickerResult(ano: Int, mes: Int, dia: Int) {
        val sDayOfMonth = if (dia < 10) "0$dia" else "$dia"
        val sMonthOfYear = if ((mes + 1) < 10) "0${mes + 1}" else "${mes + 1}"

        dataSelecionada = "$sDayOfMonth/$sMonthOfYear/$ano"
        binding.etDiligenciaData.setText(dataSelecionada)
    }
}