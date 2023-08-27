package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.advogo.R
import com.example.advogo.adapters.DiligenciasStatusAdapter
import com.example.advogo.adapters.DiligenciasTiposAdapter
import com.example.advogo.databinding.ActivityProcessoDetalheBinding
import com.example.advogo.databinding.FragmentDiligenciaDetalheBinding
import com.example.advogo.databinding.FragmentProcessoDetalheBinding
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.example.advogo.utils.SendNotificationToUserAsyncTask
import com.example.advogo.dialogs.AdvogadosDialog
import com.example.advogo.dialogs.ProcessosDialog
import com.example.advogo.utils.extensions.ConverterUtils.fromUSADateStringToDate
import com.example.advogo.utils.extensions.ConverterUtils.fromUSADateTimeStringToTimestamp
import com.example.advogo.utils.extensions.DataUtils
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DiligenciaDetalheFragment : BaseFragment() {
    private lateinit var binding: FragmentDiligenciaDetalheBinding
    private lateinit var diligenciaDetalhes: Diligencia

    @Inject lateinit var diligenciaRepository: DiligenciaRepository
    @Inject lateinit var diligenciaHistoricoRepository: DiligenciaHistoricoRepository
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
    private var advSelecionadoAnterior: String? = null
    private var advSelecionadoToken: String? = null
    private var processoSelecionado: String? = null
    private var tipoDiligenciaSelecionada: String? = null
    private var statusDiligenciaSelecionada: String? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiligenciaDetalheBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        obterIntentDados()
        configurarGoogleMapPlaces()
        setupSpinners()

        setDiligenciaToUI(diligenciaDetalhes)

        binding.btnAtualizarDiligencia.setOnClickListener {
            saveDiligencia()
        }

        binding.etDiligenciaEndereco.setOnClickListener {
            showGoogleMapPlaces(requireContext(), resultLauncher)
        }

        binding.etDiligenciaData.setOnClickListener {
            showDataPicker(requireContext()) { ano, mes, dia ->
                onDatePickerResult(ano, mes, dia)

                showTimePicker { hour, minute ->
                    onTimePickerResult(hour, minute)
                }
            }
        }

//        binding.etDiligenciaProcesso.setOnClickListener {
//            processosDialog()
//        }

        binding.etDiligenciaAdvogado.setOnClickListener {
            advogadosDialog()
        }

        binding.btnGoogleMaps.setOnClickListener {
            if(diligenciaDetalhes.enderecoLat != null && diligenciaDetalhes.enderecoLong != null) {
                openGoogleMaps(diligenciaDetalhes.enderecoLat!!, diligenciaDetalhes.enderecoLong!!)
            } else if (!diligenciaDetalhes.endereco.isNullOrBlank()) {
                openGoogleMaps(diligenciaDetalhes.endereco!!)
            } else {
                Toast.makeText(requireContext(), "Sem informação de local", Toast.LENGTH_LONG).show()
            }
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
        CoroutineScope(Dispatchers.Main).launch {
            val diligenciaStatusDeferred = async { diligenciaStatusRepository.obterDiligenciasStatus() }
            diligenciaStatus = diligenciaStatusDeferred.await()!!
            (diligenciaStatus as MutableList<DiligenciaStatus>).add(0, DiligenciaStatus(status = "Selecione"))

            val adapter = DiligenciasStatusAdapter(requireContext(), diligenciaStatus!!)
            binding.spinnerStatusDiligencia.adapter = adapter

            if(diligenciaDetalhes.statusObj != null) {
                binding.spinnerStatusDiligencia.setSelection((diligenciaStatus as MutableList<DiligenciaStatus>).indexOf(diligenciaDetalhes.statusObj))
            }

            binding.spinnerStatusDiligencia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = binding.spinnerStatusDiligencia.selectedItem as? DiligenciaStatus
                    selectedItem?.let {
                        statusDiligenciaSelecionada = selectedItem.id
                        binding.spinnerStatusDiligencia.setSelection(id.toInt())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Nada selecionado
                }
            }
        }
    }

    private fun setupSpinnerTipoDiligencia() {
        CoroutineScope(Dispatchers.Main).launch {
            val diligenciaTiposDeferred = async { diligenciaTipoRepository.obterDiligenciasTipos() }
            diligenciaTipos = diligenciaTiposDeferred.await()!!
            (diligenciaTipos as MutableList<DiligenciaTipo>).add(0, DiligenciaTipo(tipo = "Selecione"))

            val adapter = DiligenciasTiposAdapter(requireContext(), diligenciaTipos!!)
            binding.spinnerTipoDiligencia.adapter = adapter

            if(diligenciaDetalhes.tipoObj != null) {
                binding.spinnerTipoDiligencia.setSelection((diligenciaTipos as MutableList<DiligenciaTipo>).indexOf(diligenciaDetalhes.tipoObj))
            }

            binding.spinnerTipoDiligencia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = binding.spinnerTipoDiligencia.selectedItem as? DiligenciaTipo
                    selectedItem?.let {
                        tipoDiligenciaSelecionada = selectedItem.id
                        binding.spinnerTipoDiligencia.setSelection(id.toInt())
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
        advSelecionadoAnterior = diligencia.advogado
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveDiligencia() {
        if(!validarFormulario()) {
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            showProgressDialog(getString(R.string.aguardePorfavor))

            val diligenciaDetalhesDeferred = async { diligenciaRepository.obterDiligencia(diligenciaDetalhes.id!!) }
            diligenciaDetalhes = diligenciaDetalhesDeferred.await()!!

            val alteracoes = formatarAlteracoes(diligenciaDetalhes)

            val diligencia = Diligencia(
                id = diligenciaDetalhes.id,
                descricao = if (binding.etDiligenciaDescricao.text.toString() != diligenciaDetalhes.descricao) binding.etDiligenciaDescricao.text.toString() else diligenciaDetalhes.descricao,
                data = if (dataSelecionada != diligenciaDetalhes.data) dataSelecionada else diligenciaDetalhes.data,
                dataCriacao = diligenciaDetalhes.dataCriacao,
                dataCriacaoTimestamp = diligenciaDetalhes.dataCriacaoTimestamp,
                dataAlteracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                dataAlteracaoTimestamp = Timestamp.now(),
                status = if (statusDiligenciaSelecionada != diligenciaDetalhes.status) statusDiligenciaSelecionada else diligenciaDetalhes.status,
                tipo = if (tipoDiligenciaSelecionada != diligenciaDetalhes.tipo) tipoDiligenciaSelecionada else diligenciaDetalhes.tipo,
                endereco = if (binding.etDiligenciaEndereco.text.toString() != diligenciaDetalhes.endereco) binding.etDiligenciaEndereco.text.toString() else diligenciaDetalhes.endereco,
                enderecoLat = savedLatitude,
                enderecoLong = savedLongitude,
                processo = if (processoSelecionado != diligenciaDetalhes.processo) processoSelecionado else diligenciaDetalhes.processo,
                advogado = if (advSelecionado != diligenciaDetalhes.advogado) advSelecionado else diligenciaDetalhes.advogado,
                historico = diligenciaDetalhes.historico
            )

            diligencia.dataTimestamp = Timestamp(diligencia.data!!.fromUSADateStringToDate())

            diligenciaRepository.atualizarDiligencia(
                diligencia,
                {
                    val historico = DiligenciaHistorico(
                        obs = alteracoes,
                        advogado = advSelecionado,
                        status = statusDiligenciaSelecionada,
                        tipo = tipoDiligenciaSelecionada,
                        data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        dataTimestamp = Timestamp.now()
                    )

                    diligenciaHistoricoRepository.adicionarDiligenciaHistorico(
                        historico,
                        { null },
                        { null }
                    )

                    diligenciaEdicaoSuccess()
                },
                { diligenciaEdicaoFailure() }
            )
        }
    }

    private fun processosDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(processos.isEmpty()) {
                val processosDeferred = async { processoRepository.obterProcessos()!! }
                processos = processosDeferred.await()
            }

            processos.find { it.id == processoSelecionado }?.selecionado = true

            val listDialog = object : ProcessosDialog(
                requireContext(),
                processos as ArrayList<Processo>,
                resources.getString(R.string.selecionarProcesso)
            ) {
                override fun onItemSelected(processo: Processo, action: String) {
                    if (action == Constants.SELECIONAR) {
                        processos.forEach {
                            it.selecionado = false
                        }

                        if (binding.etDiligenciaProcesso.text.toString() != processo.id) {
                            binding.etDiligenciaProcesso.setText("${processo.numero}")
                            processoSelecionado = processo.id
                            processos!![processos!!.indexOf(processo)].selecionado = true
                        } else {
                            Toast.makeText(
                                requireContext(),
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
                val advogadosDeferred = async { advogadoRepository.obterAdvogados()!! }
                advogados = advogadosDeferred.await()
            }

            advogados.find { it.id == advSelecionado }?.selecionado = true

            val listDialog = object : AdvogadosDialog(
                requireContext(),
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
                            advSelecionado = adv.id
                            advSelecionadoToken = adv.fcmToken
                            advogados[advogados.indexOf(adv)].selecionado = true
                        } else {
                            Toast.makeText(
                                requireContext(),
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
        if (requireActivity().intent.hasExtra(Constants.DILIGENCIA_PARAM)) {
            diligenciaDetalhes = requireActivity().intent.getParcelableExtra<Diligencia>(Constants.DILIGENCIA_PARAM)!!
        }
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

        if(
            advSelecionado != getCurrentUserID()
            && advSelecionado != advSelecionadoAnterior
        ) {
            SendNotificationToUserAsyncTask(
                "Diligência",
                "Uma diligência foi atualizada e atribuída a você! Clique para ver tuas diligências.",
                advSelecionadoToken!!
            ).execute()
        }

        requireActivity().intent.putExtra(Constants.FROM_DILIGENCIA_ACTIVITY, Constants.FROM_DILIGENCIA_ACTIVITY)
        requireActivity().setResult(Activity.RESULT_OK, requireActivity().intent)
        requireActivity().finish()
    }

    private fun diligenciaEdicaoFailure() {
        hideProgressDialog()

        Toast.makeText(
            requireContext(),
            "Um erro ocorreu ao atualizar a diligência.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun configurarGoogleMapPlaces() {
        if(!Places.isInitialized()) {
            Places.initialize(requireContext(),
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

    private fun formatarAlteracoes(diligencia: Diligencia): String {
        var retorno = "DILIGÊNCIA ATUALIZADA"

        if(diligencia.status != statusDiligenciaSelecionada)
            retorno += "\nStatus: DE ${diligencia.statusObj!!.status} para ${(binding.spinnerStatusDiligencia.selectedItem as DiligenciaStatus).status}"

        if(diligencia.tipo != tipoDiligenciaSelecionada)
            retorno += "\nTipo: DE ${diligencia.tipoObj!!.tipo} para ${(binding.spinnerTipoDiligencia.selectedItem as DiligenciaTipo).tipo}"

        if(diligencia.processo != processoSelecionado)
            retorno += "\n Processo: para ${binding.etDiligenciaProcesso.text.toString()}"

        if(diligencia.advogado != advSelecionado)
            retorno += "\nAdvogado: para ${binding.etDiligenciaAdvogado.text.toString()}"

        if(diligencia.endereco != binding.etDiligenciaEndereco.text.toString())
            retorno += "\nEndereço para: ${binding.etDiligenciaEndereco.text.toString()}"

        if(diligencia.descricao != binding.etDiligenciaDescricao.text.toString())
            retorno += "\nDescrição atualizada"

        return retorno
    }
}