package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosStatusAdapter
import com.example.advogo.adapters.ProcessosTiposAdapter
import com.example.advogo.databinding.FragmentProcessoDetalheBinding
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.example.advogo.utils.ProcessMaskTextWatcher
import com.example.advogo.utils.SendNotificationToUserAsyncTask
import com.example.projmgr.dialogs.AdvogadosDialog
import com.example.projmgr.dialogs.ClientesDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoDetalheFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessoDetalheBinding
    private lateinit var processoDetalhes: Processo

    @Inject lateinit var processoRepository: ProcessoRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository
    @Inject lateinit var clienteRepository: ClienteRepository
    @Inject lateinit var processoTipoRepository: IProcessoTipoRepository
    @Inject lateinit var processoStatusRepository: IProcessoStatusRepository

    private var advogados: List<Advogado> = ArrayList()
    private var clientes: List<Cliente> = ArrayList()
    private var processosTipos: List<ProcessoTipo> = ArrayList()
    private var processosStatus: List<ProcessoStatus> = ArrayList()

    private var dataSelecionada: String? = null
    private var clienteSelecionado: String? = null
    private var advSelecionado: String? = null
    private var advSelecionadoToken: String? = null
    private var tipoProcessoSelecionado: String? = null
    private var statusProcessoSelecionado: String? = null

    private var imagemSelecionadaURI: Uri? = null
    private var imagemSelecionadaURL: String? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessoDetalheBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()

        setupSpinners()
        setProcessoToUI(processoDetalhes)

        binding.etNumeroProcesso.addTextChangedListener(ProcessMaskTextWatcher(binding.etNumeroProcesso))

        binding.tvSelectData.setOnClickListener {
            showDataPicker(requireContext()) { ano, mes, dia ->
                onDatePickerResult(ano, mes, dia)
            }
        }

        binding.btnProcessoCadastro.setOnClickListener {
            saveProcesso()
        }

        binding.ivProcessoImage.setOnClickListener {
            chooseImage(this@ProcessoDetalheFragment, resultLauncher)
        }

        binding.etAdv.setOnClickListener {
            advogadosDialog()
        }

        binding.etCliente.setOnClickListener {
            clientesDialog()
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagemSelecionadaURI = result.data!!.data!!

                try {
                    Glide
                        .with(this@ProcessoDetalheFragment)
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

    private suspend fun salvarImagemProcesso(): String {
        return suspendCancellableCoroutine { continuation ->
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "PROCESSO_${id}_IMAGEM" + System.currentTimeMillis() + "."
                        + getFileExtension(imagemSelecionadaURI!!)
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

    private fun saveProcesso() {
        if(!validarFormulario()) {
            return
        }

        showProgressDialog(getString(R.string.aguardePorfavor))

        CoroutineScope(Dispatchers.Main).launch {
            val imageUrl = if (imagemSelecionadaURI != null) {
                salvarImagemProcesso()
            } else {
                processoDetalhes.imagem
            }

            val processo = Processo(
                id = processoDetalhes.id,
                titulo = (if (binding.etProcessoName.text.toString() != processoDetalhes.titulo) binding.etProcessoName.text.toString() else processoDetalhes.titulo),
                descricao = (if (binding.etDescricao.text.toString() != processoDetalhes.descricao) binding.etDescricao.text.toString() else processoDetalhes.descricao),
                numero = (if (binding.etNumeroProcesso.text.toString() != processoDetalhes.numero) binding.etNumeroProcesso.text.toString() else processoDetalhes.numero),
                tipo = (if (tipoProcessoSelecionado != processoDetalhes.tipo) tipoProcessoSelecionado else processoDetalhes.tipo),
                status = (if (statusProcessoSelecionado != processoDetalhes.status) statusProcessoSelecionado else processoDetalhes.status),
                data = processoDetalhes.data,
                imagem = imageUrl,
                cliente = (if (clienteSelecionado != processoDetalhes.cliente.toString()) clienteSelecionado else processoDetalhes.cliente.toString()),
                advogado = (if (advSelecionado != processoDetalhes.advogado) advSelecionado else processoDetalhes.advogado),
                diligencias = processoDetalhes.diligencias,
                anexos = processoDetalhes.anexos,
                andamentos = processoDetalhes.andamentos
            )

            processoRepository.atualizarProcesso(
                processo,
                { atualizarProcessoSuccess() },
                { atualizarProcessoFailure() }
            )
        }
    }

    private fun setupSpinners() {
        setupSpinnerTiposProcesso()
        setupSpinnerStatusProcesso()
    }

    private fun setupSpinnerStatusProcesso() {
        val spinnerStatus = binding.spinnerStatusProcesso

        CoroutineScope(Dispatchers.Main).launch {
            val processosStatusDeferred = async { processoStatusRepository.obterProcessoStatus() }
            processosStatus = processosStatusDeferred.await()!!
            (processosStatus as MutableList<ProcessoStatus>).add(0, ProcessoStatus(status = "Selecione"))

            val adapter = ProcessosStatusAdapter(requireContext(), processosStatus)
            spinnerStatus.adapter = adapter

            if(processoDetalhes.statusObj != null)
                binding.spinnerStatusProcesso.setSelection(processosStatus.indexOf(processoDetalhes.statusObj))

            spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = spinnerStatus.selectedItem as? ProcessoStatus
                    selectedItem?.let {
                        statusProcessoSelecionado = selectedItem.id
                        spinnerStatus.setSelection(id.toInt())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Nada selecionado
                }
            }
        }
    }

    private fun setupSpinnerTiposProcesso() {
        val spinnerTipos = binding.spinnerTipoProcesso

        CoroutineScope(Dispatchers.Main).launch {
            val processosTiposDeferred = async { processoTipoRepository.obterProcessosTipos() }
            processosTipos = processosTiposDeferred.await()!!
            (processosTipos as MutableList<ProcessoTipo>).add(0, ProcessoTipo(tipo = "Selecione"))

            val adapter = ProcessosTiposAdapter(requireContext(), processosTipos)
            spinnerTipos.adapter = adapter

            if(processoDetalhes.tipoObj != null)
                binding.spinnerTipoProcesso.setSelection(processosTipos.indexOf(processoDetalhes.tipoObj))

            spinnerTipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = spinnerTipos.selectedItem as? ProcessoTipo
                    selectedItem?.let {
                        tipoProcessoSelecionado = selectedItem.id
                        spinnerTipos.setSelection(id.toInt())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Nada selecionado
                }
            }
        }
    }

    private fun advogadosDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(advogados.isEmpty()) {
                val advogadosDeferred = async { advogadoRepository.obterAdvogados()!! }
                advogados = advogadosDeferred.await()
            }

            val listDialog = object : AdvogadosDialog(
                requireContext(),
                advogados as ArrayList<Advogado>,
                resources.getString(R.string.selecionarAdvogado)
            ) {
                override fun onItemSelected(adv: Advogado, action: String?) {
                    if (action == Constants.SELECIONAR) {
                        if (binding.etAdv.text.toString() != adv.id) {
                            binding.etAdv.setText("${adv.nome} (${adv.oab})")
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
                        binding.etAdv.text = null
                        advSelecionado = null
                        advogados[advogados.indexOf(adv)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun clientesDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(clientes.isEmpty()) {
                val clientesDeferred = async { clienteRepository.obterClientes()!! }
                clientes = clientesDeferred.await()
            }

            val listDialog = object : ClientesDialog(
                requireContext(),
                clientes as ArrayList<Cliente>,
                resources.getString(R.string.selecionarCliente)
            ) {
                override fun onItemSelected(cliente: Cliente, action: String) {
                    if (action == Constants.SELECIONAR) {
                        if (binding.etCliente.text.toString() != cliente.id) {
                            binding.etCliente.setText("${cliente.nome} (${cliente.cpf})")
                            clienteSelecionado = cliente.id
                            clientes[clientes.indexOf(cliente)].selecionado = true
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Cliente já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etCliente.text = null
                        clienteSelecionado = null
                        clientes[clientes.indexOf(cliente)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun setProcessoToUI(processo: Processo) {
        binding.etProcessoName.setText(processo.titulo)
        binding.etDescricao.setText(processo.descricao)
        binding.spinnerTipoProcesso.setSelection(processosTipos.indexOf(processo.tipoObj))
        binding.spinnerStatusProcesso.setSelection(processosStatus.indexOf(processo.statusObj))
        binding.etNumeroProcesso.setText(processo.numero)
        binding.etAdv.setText(processo.advogadoObj?.nome)
        binding.etCliente.setText(processo.clienteObj?.nome)

        advSelecionado = processoDetalhes.advogado
        clienteSelecionado = processoDetalhes.cliente
        tipoProcessoSelecionado = processoDetalhes.tipo
        statusProcessoSelecionado = processoDetalhes.status
        dataSelecionada = processoDetalhes.data

        if(!dataSelecionada.isNullOrEmpty()) {
            val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val fromDate = fromFormat.parse(dataSelecionada)
            val selectedDate = toFormat.format(fromDate)
            binding.etData.setText(selectedDate)
        }

        Glide
            .with(requireContext())
            .load(processoDetalhes.imagem)
            .centerCrop()
            .placeholder(R.drawable.ic_processo_place_holder)
            .into(binding.ivProcessoImage)
    }

    private fun validarFormulario(): Boolean {
        var validado = true

        if (TextUtils.isEmpty(binding.etProcessoName.text.toString())) {
            binding.etProcessoName.error = "Obrigatório"
            binding.etProcessoName.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etNumeroProcesso.text.toString())) {
            binding.etNumeroProcesso.error = "Obrigatório"
            binding.etNumeroProcesso.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(binding.etDescricao.text.toString())) {
            binding.etDescricao.error = "Obrigatório"
            binding.etDescricao.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(tipoProcessoSelecionado)) {
            validado = false
        }

        if (TextUtils.isEmpty(statusProcessoSelecionado)) {
            validado = false
        }

        if (TextUtils.isEmpty(dataSelecionada)) {
            binding.etData.error = "Obrigatório"
            binding.etData.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(clienteSelecionado)) {
            binding.etCliente.error = "Obrigatório"
            binding.etCliente.requestFocus()
            validado = false
        }

        if (TextUtils.isEmpty(advSelecionado)) {
            binding.etAdv.error = "Obrigatório"
            binding.etAdv.requestFocus()
            validado = false
        }

        return validado
    }

    private fun obterIntentDados() {
        if (requireActivity().intent.hasExtra(Constants.PROCESSO_PARAM)) {
            processoDetalhes = requireActivity().intent.getParcelableExtra<Processo>(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun onDatePickerResult(year: Int, month: Int, day: Int) {
        val sDayOfMonth = if (day < 10) "0$day" else "$day"
        val sMonthOfYear = if ((month + 1) < 10) "0${month + 1}" else "${month + 1}"

        dataSelecionada = "$year-$sMonthOfYear-$sDayOfMonth"
        binding.tvSelectData.text = "$sDayOfMonth/$sMonthOfYear/$year"
    }

    private fun atualizarProcessoSuccess() {
        hideProgressDialog()

        if(
            advSelecionado != getCurrentUserID()
        //TODO("Incluir validação para verificar se o advSelecionado é diferente do anterior. Só mandar notificação se for diferente")
        //&& advSelecionado != advSelecionadoAnterior
        ) {
            SendNotificationToUserAsyncTask(
                "Processo",
                "Um processo foi atualizado e atribuído a você! Clique para ver teus processos.",
                advSelecionadoToken!!
            ).execute()
        }

        val activity = requireActivity()
        activity.intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
        activity.setResult(Activity.RESULT_OK, requireActivity().intent)
        activity.finish()
    }

    private fun atualizarProcessoFailure() {
        hideProgressDialog()
    }
}