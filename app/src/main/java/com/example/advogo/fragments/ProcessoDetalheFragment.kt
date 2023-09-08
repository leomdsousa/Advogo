package com.example.advogo.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.adapters.spinner.ProcessosStatusAdapter
import com.example.advogo.databinding.FragmentProcessoDetalheBinding
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.example.advogo.utils.SendNotificationToUserAsyncTask
import com.example.advogo.dialogs.AdvogadosDialog
import com.example.advogo.dialogs.ClientesDialog
import com.example.advogo.dialogs.ProcessoStatusDialog
import com.example.advogo.dialogs.ProcessoTiposDialog
import com.example.advogo.utils.extensions.ConverterUtils.fromUSADateStringToDate
import com.example.advogo.utils.extensions.DataUtils
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ProcessoDetalheFragment : BaseFragment() {
    private lateinit var binding: FragmentProcessoDetalheBinding
    private lateinit var processoDetalhes: Processo

    @Inject lateinit var processoRepository: ProcessoRepository
    @Inject lateinit var processoHistoricoRepository: ProcessoHistoricoRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository
    @Inject lateinit var clienteRepository: ClienteRepository
    @Inject lateinit var processoTipoRepository: IProcessoTipoRepository
    @Inject lateinit var processoStatusRepository: IProcessoStatusRepository

    private var advogados: List<Advogado> = ArrayList()
    private var processosTipos: List<ProcessoTipo> = ArrayList()
    private var processosStatus: List<ProcessoStatus> = ArrayList()

    private var dataSelecionada: String? = null
    private var clienteSelecionado: String? = null
    private var advSelecionado: String? = null
    private var advSelecionadoAnterior: String? = null
    private var advSelecionadoToken: String? = null
    private var tipoProcessoSelecionado: String? = null
    private var statusProcessoSelecionado: String? = null

    private var imagemSelecionadaURI: Uri? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProcessoDetalheBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obterIntentDados()
        setProcessoToUI(processoDetalhes)

        binding.btnProcessoCadastro.setOnClickListener {
            saveProcesso()
        }

        binding.ivProcessoImage.setOnClickListener {
            chooseImage(this@ProcessoDetalheFragment, resultLauncher)
        }

        binding.etAdv.setOnClickListener {
            advogadosDialog()
        }

        binding.etTipoProcesso.setOnClickListener {
            tiposProcessoDialog()
        }

        binding.etStatusProcesso.setOnClickListener {
            statusProcessoDialog()
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

    @RequiresApi(Build.VERSION_CODES.O)
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

            val processoDetalhesDeferred = async { processoRepository.obterProcesso(processoDetalhes.id!!) }
            processoDetalhes = processoDetalhesDeferred.await()!!

            val alteracoes = formatarAlteracoes(processoDetalhes)

            val processo = Processo(
                id = processoDetalhes.id,
                titulo = (if (binding.etProcessoName.text.toString() != processoDetalhes.titulo) binding.etProcessoName.text.toString() else processoDetalhes.titulo),
                descricao = (if (binding.etDescricao.text.toString() != processoDetalhes.descricao) binding.etDescricao.text.toString() else processoDetalhes.descricao),
                numero = (if (binding.etNumeroProcesso.text.toString() != processoDetalhes.numero) binding.etNumeroProcesso.text.toString() else processoDetalhes.numero),
                tipo = (if (tipoProcessoSelecionado != processoDetalhes.tipo) tipoProcessoSelecionado else processoDetalhes.tipo),
                status = (if (statusProcessoSelecionado != processoDetalhes.status) statusProcessoSelecionado else processoDetalhes.status),
                data = processoDetalhes.data,
                dataCriacao = processoDetalhes.dataCriacao,
                dataCriacaoTimestamp = processoDetalhes.dataCriacaoTimestamp,
                dataAlteracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                dataAlteracaoTimestamp = Timestamp.now(),
                imagem = imageUrl,
                cliente = (if (clienteSelecionado != processoDetalhes.cliente.toString()) clienteSelecionado else processoDetalhes.cliente.toString()),
                advogado = (if (advSelecionado != processoDetalhes.advogado) advSelecionado else processoDetalhes.advogado),
                diligencias = processoDetalhes.diligencias,
                anexos = processoDetalhes.anexos,
                andamentos = processoDetalhes.andamentos,
                historico = processoDetalhes.historico
            )

            processo.dataTimestamp = Timestamp(processo.data!!.fromUSADateStringToDate())

            processoRepository.atualizarProcesso(
                processo,
                {
                    val historico = ProcessoHistorico(
                        obs = alteracoes,
                        advogado = advSelecionado,
                        status = statusProcessoSelecionado,
                        tipo = tipoProcessoSelecionado,
                        processo = processoDetalhes.numero,
                        data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        dataTimestamp = Timestamp.now()
                    )

                    processoHistoricoRepository.adicionarProcessoHistorico(
                        historico,
                        { null },
                        { null }
                    )

                    atualizarProcessoSuccess()
                },
                { atualizarProcessoFailure() }
            )
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

    private fun tiposProcessoDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(processosTipos.isEmpty()) {
                val tiposProcessoDeferred = async { processoTipoRepository.obterProcessosTipos ()!! }
                processosTipos = tiposProcessoDeferred.await()
            }

            val listDialog = object : ProcessoTiposDialog(
                requireContext(),
                processosTipos,
            ) {
                override fun onItemSelected(tipoProcesso: ProcessoTipo, action: String) {
                    if (action == Constants.SELECIONAR) {
                        processosTipos.forEach {
                            it.selecionado = false
                        }

                        if (binding.etTipoProcesso.text.toString() != tipoProcesso.id) {
                            binding.etTipoProcesso.setText(tipoProcesso.tipo)
                            tipoProcessoSelecionado = tipoProcesso.id
                            processosTipos[processosTipos.indexOf(tipoProcesso)].selecionado = true
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Tipo já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etTipoProcesso.text = null
                        tipoProcessoSelecionado = null
                        processosTipos[processosTipos.indexOf(tipoProcesso)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun statusProcessoDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(processosStatus.isEmpty()) {
                val statusProcessoDeferred = async { processoStatusRepository.obterProcessoStatus() }
                processosStatus = statusProcessoDeferred.await()!!
            }

            val listDialog = object : ProcessoStatusDialog(
                requireContext(),
                processosStatus,
            ) {
                override fun onItemSelected(item: ProcessoStatus, action: String) {
                    if (action == Constants.SELECIONAR) {
                        processosStatus.forEach {
                            it.selecionado = false
                        }

                        if (binding.etStatusProcesso.text.toString() != item.id) {
                            binding.etStatusProcesso.setText(item.status)
                            statusProcessoSelecionado = item.id
                            processosStatus[processosStatus.indexOf(item)].selecionado = true
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Status já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etStatusProcesso.text = null
                        statusProcessoSelecionado = null
                        processosStatus[processosStatus.indexOf(item)].selecionado = false
                    }
                }
            }

            listDialog.show()
        }
    }

    private fun setProcessoToUI(processo: Processo) {
        binding.etProcessoName.setText(processo.titulo)
        binding.etDescricao.setText(processo.descricao)
        binding.etTipoProcesso.setText(processo.tipoObj?.tipo)
        binding.etStatusProcesso.setText(processo.statusObj?.status)
        binding.etNumeroProcesso.setText(processo.numero)
        binding.etAdv.setText("${processo.advogadoObj?.nome} (${processo.advogadoObj?.oab})")
        binding.etCliente.setText("${processo.clienteObj?.nome} (${processo.clienteObj?.cpf})")

        advSelecionado = processoDetalhes.advogado
        advSelecionadoAnterior = processoDetalhes.advogado
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
            .placeholder(R.drawable.image_placeholder)
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

    private fun atualizarProcessoSuccess() {
        hideProgressDialog()

        if(
            advSelecionado != getCurrentUserID()
            && advSelecionado != advSelecionadoAnterior
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

    private fun formatarAlteracoes(processo: Processo): String {
        var retorno = "PROCESSO ATUALIZADO"

        if(processo.status != statusProcessoSelecionado)
            retorno += "\nStatus: DE ${processo.statusObj!!.status} para ${binding.etStatusProcesso.text.toString()}"

        if(processo.tipo != tipoProcessoSelecionado)
            retorno += "\nTipo: DE ${processo.tipoObj!!.tipo} para ${binding.etTipoProcesso.text.toString()}"

        if(processo.advogado != advSelecionado)
            retorno += "\nAdvogado: para ${binding.etAdv.text.toString()}"

        if(processo.cliente != clienteSelecionado)
            retorno += "\nCliente: para ${binding.etCliente.text.toString()}"

        if(processo.numero != binding.etNumeroProcesso .text.toString())
            retorno += "\nNº Processo atualizado"

        if(processo.titulo != binding.etProcessoName.text.toString())
            retorno += "\nTítulo atualizado"

        if(processo.descricao != binding.etDescricao.text.toString())
            retorno += "\nDescrição atualizada"

        if(imagemSelecionadaURI != null)
            retorno += "\nImagem atualizada"

        return retorno
    }
}