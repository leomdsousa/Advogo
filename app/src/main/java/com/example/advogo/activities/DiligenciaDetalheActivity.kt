package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.advogo.R
import com.example.advogo.adapters.DiligenciasStatusAdapter
import com.example.advogo.adapters.DiligenciasTiposAdapter
import com.example.advogo.databinding.ActivityDiligenciaDetalheBinding
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.example.advogo.utils.Constants.DILIGENCIA_MAP
import com.example.projmgr.dialogs.AdvogadosDialog
import com.example.projmgr.dialogs.ProcessosDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
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
class DiligenciaDetalheActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaDetalheBinding
    private lateinit var diligenciaDetalhes: Diligencia

    @Inject lateinit var diligenciaRepository: DiligenciaRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository
    @Inject lateinit var clienteRepository: ClienteRepository
    @Inject lateinit var processoRepository: ProcessoRepository
    @Inject lateinit var diligenciaTipoRepository: DiligenciaTipoRepository
    @Inject lateinit var diligenciaStatusRepository: DiligenciaStatusRepository

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var advogados: List<Advogado> = ArrayList()
    private var processos: List<Processo> = ArrayList()
    private var diligenciaStatus: List<DiligenciaStatus>? = ArrayList()
    private var diligenciaTipos: List<DiligenciaTipo>? = ArrayList()

    private var savedLatitude: Double = 0.0
    private var savedLongitude: Double = 0.0
    private var dataSelecionada: String? = null
    private var advSelecionado: String? = null
    private var processoSelecionado: String? = null
    private var tipoDiligenciaSelecionada: String? = null
    private var statusDiligenciaSelecionada: String? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiligenciaDetalheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        obterIntentDados()
        setupActionBar("Detalhe Diligência", binding.toolbarDiligenciaDetalhe)
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

        binding.etDiligenciaProcesso.setOnClickListener {
            processosDialog()
        }

        binding.etDiligenciaAdvogado.setOnClickListener {
            advogadosDialog()
        }

        binding.btnGoogleMaps.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra(Constants.DILIGENCIA_MAP, diligenciaDetalhes)
            startActivity(intent)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val data: Intent? = result.data
                    if(data != null) {
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
            (diligenciaStatus as MutableList<DiligenciaStatus>).add(0, DiligenciaStatus(status = "Selecione"))

            val adapter = DiligenciasStatusAdapter(this@DiligenciaDetalheActivity, diligenciaStatus!!)
            spinnerStatus.adapter = adapter

            if(diligenciaDetalhes.statusObj != null) {
                binding.spinnerStatusDiligencia.setSelection((diligenciaStatus as MutableList<DiligenciaStatus>).indexOf(diligenciaDetalhes.statusObj))
            }

            spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = spinnerStatus.selectedItem as? DiligenciaStatus
                    selectedItem?.let {
                        statusDiligenciaSelecionada = selectedItem.id
                        spinnerStatus.setSelection(id.toInt())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Nada selecionado
                }
            }
        }
    }

    private fun setupSpinnerTipoDiligencia() {
        val spinnerTipos = findViewById<Spinner>(R.id.spinnerTipoDiligencia)

        CoroutineScope(Dispatchers.Main).launch {
            val diligenciaTiposDeferred = async { diligenciaTipoRepository.ObterDiligenciasTipos() }
            diligenciaTipos = diligenciaTiposDeferred.await()!!
            (diligenciaTipos as MutableList<DiligenciaTipo>).add(0, DiligenciaTipo(tipo = "Selecione"))

            val adapter = DiligenciasTiposAdapter(this@DiligenciaDetalheActivity, diligenciaTipos!!)
            spinnerTipos.adapter = adapter

            if(diligenciaDetalhes.tipoObj != null) {
                binding.spinnerTipoDiligencia.setSelection((diligenciaTipos as MutableList<DiligenciaTipo>).indexOf(diligenciaDetalhes.tipoObj))
            }

            spinnerTipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = spinnerTipos.selectedItem as? DiligenciaTipo
                    selectedItem?.let {
                        tipoDiligenciaSelecionada = selectedItem.id
                        spinnerTipos.setSelection(id.toInt())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Nada selecionado
                }
            }
        }
    }

    private fun setDiligenciaToUI(diligencia: Diligencia) {
        binding.etDiligenciaDescricao.setText(diligencia.descricao)
        binding.spinnerTipoDiligencia.setSelection(diligenciaTipos!!.indexOf(diligencia.tipoObj!!))
        binding.spinnerStatusDiligencia.setSelection(diligenciaStatus!!.indexOf(diligencia.statusObj!!))
        binding.etDiligenciaProcesso.setText(diligencia.processoObj?.numero)
        binding.etDiligenciaAdvogado.setText("${diligencia.advogadoObj?.nome} (${diligencia.advogadoObj?.oab})")
        binding.etDiligenciaEndereco.setText(diligencia.endereco)

        dataSelecionada = diligencia.data
        processoSelecionado = diligencia.processo
        advSelecionado = diligencia.advogado
        tipoDiligenciaSelecionada = diligencia.tipo
        statusDiligenciaSelecionada = diligencia.status

        if(!dataSelecionada.isNullOrEmpty()) {
            val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val fromDate = fromFormat.parse(dataSelecionada)
            val selectedDate = toFormat.format(fromDate)
            binding.etDiligenciaData.setText(selectedDate)
        }
    }

    private fun saveDiligencia() {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val diligencia = Diligencia(
            id = diligenciaDetalhes.id,
            descricao = if (binding.etDiligenciaDescricao.text.toString() != diligenciaDetalhes.descricao) binding.etDiligenciaDescricao.text.toString() else diligenciaDetalhes.descricao,
            data = if (dataSelecionada != diligenciaDetalhes.data) dataSelecionada else diligenciaDetalhes.data,
            status = if (statusDiligenciaSelecionada != diligenciaDetalhes.status) statusDiligenciaSelecionada else diligenciaDetalhes.status,
            tipo = if (tipoDiligenciaSelecionada != diligenciaDetalhes.tipo) tipoDiligenciaSelecionada else diligenciaDetalhes.tipo,
            endereco = if (binding.etDiligenciaEndereco.text.toString() != diligenciaDetalhes.endereco) binding.etDiligenciaEndereco.text.toString() else diligenciaDetalhes.endereco,
            enderecoLat = 0.0,
            enderecoLong = 0.0,
            processo = if (binding.etDiligenciaProcesso.text.toString() != diligenciaDetalhes.processo) binding.etDiligenciaProcesso.text.toString() else diligenciaDetalhes.processo,
            advogado = if (processoSelecionado != diligenciaDetalhes.processo) processoSelecionado else diligenciaDetalhes.processo,
        )

        diligenciaRepository.AtualizarDiligencia(
            diligencia,
            { diligenciaEdicaoSuccess() },
            { diligenciaEdicaoFailure() }
        )
    }

    private fun processosDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(processos == null) {
                val processosDeferred = async { processoRepository.ObterProcessos()!! }
                processos = processosDeferred.await()
            }

            val listDialog = object : ProcessosDialog(
                this@DiligenciaDetalheActivity,
                processos as ArrayList<Processo>,
                resources.getString(R.string.selecionarProcesso)
            ) {
                override fun onItemSelected(processo: Processo, action: String) {
                    if (action == Constants.SELECIONAR) {
                        if (binding.etDiligenciaProcesso.text.toString() != processo.id) {
                            binding.etDiligenciaProcesso.setText(processo.id)
                            processoSelecionado = processo.id
                            processos!![processos!!.indexOf(processo)].selecionado = true
                        } else {
                            Toast.makeText(
                                this@DiligenciaDetalheActivity,
                                "Processo já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etDiligenciaProcesso.text = null
                        processoSelecionado = null
                        processos!![processos!!.indexOf(processo)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun advogadosDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(advogados.isEmpty()) {
                val advogadosDeferred = async { advogadoRepository.ObterAdvogados()!! }
                advogados = advogadosDeferred.await()
            }

            val listDialog = object : AdvogadosDialog(
                this@DiligenciaDetalheActivity,
                advogados as ArrayList<Advogado>,
                resources.getString(R.string.selecionarAdvogado)
            ) {
                override fun onItemSelected(adv: Advogado, action: String) {
                    if (action == Constants.SELECIONAR) {
                        if (binding.etDiligenciaAdvogado.text.toString() != adv.id) {
                            binding.etDiligenciaAdvogado.setText("${adv.nome} (${adv.oab})")
                            advSelecionado = adv.id
                            advogados[advogados.indexOf(adv)].selecionado = true
                        } else {
                            Toast.makeText(
                                this@DiligenciaDetalheActivity,
                                "Advogado já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etDiligenciaAdvogado.text = null
                        advSelecionado = null
                        advogados[advogados.indexOf(adv)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
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
            android.R.id.home -> {
                onBackPressed()
                return true
            }
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
            diligenciaDetalhes.id,
            { deletarDiligenciaSuccess() },
            { deletarDiligenciaFailure() }
        )
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(binding.etDiligenciaDescricao.text.toString())) {
            binding.etDiligenciaDescricao.error = "Obrigatório"
            binding.etDiligenciaDescricao.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etDiligenciaData.text.toString())) {
            binding.etDiligenciaData.error = "Obrigatório"
            binding.etDiligenciaData.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etDiligenciaEndereco.text.toString())) {
            binding.etDiligenciaEndereco.error = "Obrigatório"
            binding.etDiligenciaEndereco.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(tipoDiligenciaSelecionada)) {
            validado = false
        }

        if (TextUtils.isEmpty(statusDiligenciaSelecionada)) {
            validado = false
        }

        if (TextUtils.isEmpty(dataSelecionada)) {
            validado = false
        }

        if (TextUtils.isEmpty(advSelecionado)) {
            binding.etDiligenciaAdvogado.error = "Obrigatório"
            binding.etDiligenciaAdvogado.requestFocus()
            validado = false
        }

        return validado
    }

    private fun diligenciaEdicaoSuccess() {
        hideProgressDialog()

        intent.putExtra(Constants.FROM_DILIGENCIA_ACTIVITY, Constants.FROM_DILIGENCIA_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun diligenciaEdicaoFailure() {
        hideProgressDialog()

        Toast.makeText(
            this@DiligenciaDetalheActivity,
            "Um erro ocorreu ao atualizar a diligência.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deletarDiligenciaSuccess() {
        hideProgressDialog()

        intent.putExtra(Constants.FROM_DILIGENCIA_ACTIVITY, Constants.FROM_DILIGENCIA_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun deletarDiligenciaFailure() {
        hideProgressDialog()
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

        dataSelecionada = "$ano-$sMonthOfYear-$sDayOfMonth"
        binding.etDiligenciaData.setText("$sDayOfMonth/$sMonthOfYear/$ano")
    }
}