package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.advogo.R
import com.example.advogo.databinding.ActivityDiligenciaCadastroBinding
import com.example.advogo.dialogs.*
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.UserUtils.getCurrentUserID
import com.example.advogo.utils.constants.Constants
import com.example.advogo.utils.notification.SendNotificationToUserAsyncTask
import com.example.advogo.utils.extensions.DataUtils
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DiligenciaCadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaCadastroBinding

    @Inject lateinit var diligenciaRepository: DiligenciaRepository
    @Inject lateinit var diligenciaHistoricoRepository: DiligenciaHistoricoRepository
    @Inject lateinit var diligenciaTipoRepository: DiligenciaTipoRepository
    @Inject lateinit var diligenciaStatusRepository: DiligenciaStatusRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository
    @Inject lateinit var processoRepository: ProcessoRepository

    private var advogados: List<Advogado> = ArrayList()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var savedLatitude: Double = 0.0
    private var savedLongitude: Double = 0.0
    private var dataSelecionada: String? = null
    private var processoSelecionado: String? = null
    private var tipoDiligenciaSelecionada: String? = null
    private var statusDiligenciaSelecionada: String? = null
    private var advogadoSelecionado: String? = null
    private var advogadoSelecionadoToken: String? = null

    private var diligenciaStatus: List<DiligenciaStatus> = ArrayList()
    private var diligenciaTipos: List<DiligenciaTipo> = ArrayList()
    private var processos: List<Processo> = ArrayList()

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiligenciaCadastroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupActionBar("Cadastro Diligência", binding.toolbarDiligenciaCadastro)
        configurarGoogleMapPlaces()

        binding.btnCadastrarDiligencia.setOnClickListener {
            saveDiligencia()
        }

        binding.etDiligenciaEndereco.setOnClickListener {
            showGoogleMapPlaces(this, resultLauncher)
        }

        binding.etDiligenciaData.setOnClickListener {
            showDataPicker() { ano, mes, dia ->
                onDatePickerResult(ano, mes, dia)

                showTimePicker { hour, minute ->
                    onTimePickerResult(hour, minute)
                }
            }
        }

        binding.etDiligenciaProcesso.setOnClickListener {
            processosDialog()
        }

        binding.etDiligenciaAdvogado.setOnClickListener {
            advogadosDialog()
        }

        binding.etTipoDiligencia.setOnClickListener {
            tiposDiligenciaDialog()
        }

        binding.etStatusDiligencia.setOnClickListener {
            statusDiligenciaDialog()
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

    private fun tiposDiligenciaDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(diligenciaTipos.isEmpty()) {
                val tiposDiligenciaDeferred = async { diligenciaTipoRepository.obterDiligenciasTipos()!! }
                diligenciaTipos = tiposDiligenciaDeferred.await()
            }

            val listDialog = object : DiligenciaTiposDialog(
                this@DiligenciaCadastroActivity,
                diligenciaTipos,
            ) {
                override fun onItemSelected(item: DiligenciaTipo, action: String) {
                    if (action == Constants.SELECIONAR) {
                        diligenciaTipos.forEach {
                            it.selecionado = false
                        }

                        if (binding.etTipoDiligencia.text.toString() != item.id) {
                            binding.etTipoDiligencia.setText(item.tipo)
                            tipoDiligenciaSelecionada = item.id
                            diligenciaTipos[diligenciaTipos.indexOf(item)].selecionado = true
                        } else {
                            Toast.makeText(
                                this@DiligenciaCadastroActivity,
                                "Tipo já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etTipoDiligencia.text = null
                        tipoDiligenciaSelecionada = null
                        diligenciaTipos[diligenciaTipos.indexOf(item)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun statusDiligenciaDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(diligenciaStatus.isEmpty()) {
                val statusProcessoDeferred = async { diligenciaStatusRepository.obterDiligenciasStatus() }
                diligenciaStatus = statusProcessoDeferred.await()!!
            }

            val listDialog = object : DiligenciaStatusDialog(
                this@DiligenciaCadastroActivity,
                diligenciaStatus,
            ) {
                override fun onItemSelected(item: DiligenciaStatus, action: String) {
                    if (action == Constants.SELECIONAR) {
                        diligenciaStatus.forEach {
                            it.selecionado = false
                        }

                        if (binding.etStatusDiligencia.text.toString() != item.id) {
                            binding.etStatusDiligencia.setText(item.status)
                            statusDiligenciaSelecionada = item.id
                            diligenciaStatus[diligenciaStatus.indexOf(item)].selecionado = true
                        } else {
                            Toast.makeText(
                                this@DiligenciaCadastroActivity,
                                "Status já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etStatusDiligencia.text = null
                        statusDiligenciaSelecionada = null
                        diligenciaStatus[diligenciaStatus.indexOf(item)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun processosDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(processos.isEmpty()) {
                val processosDeferred = async { processoRepository.obterProcessos()!! }
                processos = processosDeferred.await()
            }

            val listDialog = object : ProcessosDialog(
                this@DiligenciaCadastroActivity,
                processos as ArrayList<Processo>,
                resources.getString(R.string.selecionarProcesso)
            ) {
                override fun onItemSelected(processo: Processo, action: String) {
                    if (action == Constants.SELECIONAR) {
                        if (binding.etDiligenciaProcesso.text.toString() != processo.id) {
                            binding.etDiligenciaProcesso.setText("${processo.numero}")
                            processoSelecionado = processo.id
                            processos[processos.indexOf(processo)].selecionado = true
                        } else {
                            Toast.makeText(
                                this@DiligenciaCadastroActivity,
                                "Processo já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etDiligenciaProcesso.text = null
                        processoSelecionado = null
                        processos[processos.indexOf(processo)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun advogadosDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(advogados.isEmpty()) {
                val advogadosDeferred = async { advogadoRepository.obterAdvogados()!! }
                advogados = advogadosDeferred.await()
            }

            val listDialog = object : AdvogadosDialog(
                this@DiligenciaCadastroActivity,
                advogados as ArrayList<Advogado>,
                resources.getString(R.string.selecionarAdvogado)
            ) {
                override fun onItemSelected(adv: Advogado, action: String?) {
                    if (action == Constants.SELECIONAR) {
                        advogados.forEach {
                            it.selecionado = false
                        }

                        if (binding.etDiligenciaAdvogado.text.toString() != adv.id) {
                            binding.etDiligenciaAdvogado.setText("${adv.nome} (${adv.oab})")
                            advogadoSelecionado = adv.id
                            advogadoSelecionadoToken = adv.fcmToken
                            advogados[advogados.indexOf(adv)].selecionado = true
                        } else {
                            Toast.makeText(
                                this@DiligenciaCadastroActivity,
                                "Advogado já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etDiligenciaAdvogado.text = null
                        advogadoSelecionado = adv.id
                        advogados[advogados.indexOf(adv)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDiligencia() {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        val diligencia = Diligencia(
            id = "",
            descricao = binding.etDiligenciaDescricao.text.toString(),
            data = dataSelecionada,
            dataTimestamp = Timestamp(dataSelecionada!!.fromUSADateStringToDate()),
            dataCriacao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            dataCriacaoTimestamp = Timestamp.now(),
            dataAlteracao = null,
            dataAlteracaoTimestamp = null,
            tipo = tipoDiligenciaSelecionada,
            status = statusDiligenciaSelecionada,
            endereco = binding.etDiligenciaEndereco.text.toString(),
            enderecoLat = savedLatitude,
            enderecoLong = savedLongitude,
            processo = processoSelecionado,
            advogado = advogadoSelecionado
        )

        diligenciaRepository.adicionarDiligencia(
            diligencia,
            {   generatedId ->
                val historico = DiligenciaHistorico(
                    obs = "DILIGÊNCIA CADASTRADA",
                    advogado = advogadoSelecionado,
                    status = statusDiligenciaSelecionada,
                    tipo = tipoDiligenciaSelecionada,
                    diligencia = generatedId,
                    data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    dataTimestamp = Timestamp.now()
                )

                diligenciaHistoricoRepository.adicionarDiligenciaHistorico(
                    historico,
                    { null },
                    { null }
                )

                diligenciaCadastroSuccess()
            },
            { diligenciaCadastroFailure() }
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

        if (TextUtils.isEmpty(advogadoSelecionado)) {
            binding.etDiligenciaAdvogado.error = "Obrigatório"
            binding.etDiligenciaAdvogado.requestFocus()
            validado = false
        }

        return validado
    }

    private fun diligenciaCadastroSuccess() {
        hideProgressDialog()

        if(advogadoSelecionado != getCurrentUserID()) {
            SendNotificationToUserAsyncTask(
                "Diligência",
                "Nova diligência cadastrada e desginada para você! Clique para ver tuas diligências.",
                advogadoSelecionadoToken!!
            ).execute()
        }

        intent.putExtra(Constants.FROM_DILIGENCIA_ACTIVITY, Constants.FROM_DILIGENCIA_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun diligenciaCadastroFailure() {
        hideProgressDialog()

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDatePickerResult(ano: Int, mes: Int, dia: Int) {
        val retorno = DataUtils.onDatePickerResult(ano, mes, dia)

        dataSelecionada = retorno.dataUSA
        binding.etDiligenciaData.setText(retorno.dataBR)
    }

    private fun onTimePickerResult(hora: Int, minuto: Int) {
        val horaAux = if (hora < 10) "0$hora" else "$hora"
        val minutoAux = if (minuto < 10) "0$minuto" else "$minuto"

        dataSelecionada = "$dataSelecionada $horaAux:$minutoAux:00"
        val atualValorDiligenciaData = binding.etDiligenciaData.text.toString()
        binding.etDiligenciaData.setText("$atualValorDiligenciaData $horaAux:$minutoAux")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}