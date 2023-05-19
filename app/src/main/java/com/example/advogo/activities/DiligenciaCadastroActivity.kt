package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.advogo.R
import com.example.advogo.adapters.DiligenciasStatusAdapter
import com.example.advogo.adapters.DiligenciasTiposAdapter
import com.example.advogo.databinding.ActivityDiligenciaCadastroBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.Diligencia
import com.example.advogo.models.DiligenciaStatus
import com.example.advogo.models.DiligenciaTipo
import com.example.advogo.repositories.AdvogadoRepository
import com.example.advogo.repositories.DiligenciaRepository
import com.example.advogo.repositories.DiligenciaStatusRepository
import com.example.advogo.repositories.DiligenciaTipoRepository
import com.example.advogo.utils.Constants
import com.example.projmgr.dialogs.AdvogadosDialog
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

class DiligenciaCadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaCadastroBinding
    @Inject lateinit var diligenciaRepository: DiligenciaRepository
    @Inject lateinit var diligenciaTipoRepository: DiligenciaTipoRepository
    @Inject lateinit var diligenciaStatusRepository: DiligenciaStatusRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository

    private var advogados: List<Advogado> = ArrayList()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    private var savedUriImage: Uri? = null
    private var savedLatitude: Double = 0.0
    private var savedLongitude: Double = 0.0
    private var dataSelecionada: String? = null

    private var diligenciaStatus: List<DiligenciaStatus>? = ArrayList()
    private var diligenciaTipos: List<DiligenciaTipo>? = ArrayList()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiligenciaCadastroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupActionBar()
        configurarGoogleMapPlaces()
        setupSpinners()

        binding.btnCadastrarDiligencia.setOnClickListener {
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

        binding.etDiligenciaAdvogado.setOnClickListener {
            advogadosDialog()
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

    private fun advogadosDialog() {
        if(advogados.isEmpty()) {
            advogados = carregarAdvogados()
        }

        val listDialog = object : AdvogadosDialog(
            this@DiligenciaCadastroActivity,
            advogados as ArrayList<Advogado>,
            resources.getString(R.string.selecionarAdvogado)
        ) {
            override fun onItemSelected(adv: Advogado, action: String) {
                if (action == Constants.SELECIONAR) {
//                    if (processoDetalhes.advogado != adv.id) {
//                        processoDetalhes.advogado = adv.id
//                        advogados[advogados.indexOf(adv)].selecionado = true
//                    } else {
//                        Toast.makeText(
//                            this@DiligenciaCadastroActivity,
//                            "Advogado já selecionado! Favor escolher outro.",
//                            Toast.LENGTH_LONG
//                        ).show()
//                    }
                } else {
//                    processoDetalhes.advogado = null
//                    advogados[advogados.indexOf(adv)].selecionado = false
                }
            }
        }

        listDialog.show()
    }

    private fun carregarAdvogados(): List<Advogado> {
        var retorno: List<Advogado> = ArrayList()

        advogadoRepository.ObterAdvogados(
            { lista -> retorno = lista },
            { null } //TODO("Implementar")
        )

        return retorno
    }

    private fun setupSpinners() {
        setupSpinnerTipoDiligencia()
        setupSpinnerStatusDiligencia()
    }

    private fun setupSpinnerStatusDiligencia() {
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatusProcesso)

        CoroutineScope(Dispatchers.Main).launch {
            val diligenciaStatusDeferred = async { diligenciaStatusRepository.ObterDiligenciasStatus() }
            diligenciaStatus = diligenciaStatusDeferred.await()!!

            val adapter = DiligenciasStatusAdapter(this@DiligenciaCadastroActivity, diligenciaStatus!!)
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
        val spinnerTipos = findViewById<Spinner>(R.id.spinnerTipoProcesso)

        CoroutineScope(Dispatchers.Main).launch {
            val diligenciaTiposDeferred = async { diligenciaTipoRepository.ObterDiligenciasTipos() }
            diligenciaTipos = diligenciaTiposDeferred.await()!!

            val adapter = DiligenciasTiposAdapter(this@DiligenciaCadastroActivity, diligenciaTipos!!)
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

        diligenciaRepository.AdicionarDiligencia(
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

    private fun configurarGoogleMapPlaces() {
        if(!Places.isInitialized()) {
            Places.initialize(this@DiligenciaCadastroActivity,
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