package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.dialogs.AdvogadosDialog
import com.example.advogo.dialogs.ClientesDialog
import com.example.advogo.dialogs.ProcessoStatusDialog
import com.example.advogo.dialogs.ProcessoTiposDialog
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.UserUtils.getCurrentUserID
import com.example.advogo.utils.constants.Constants
import com.example.advogo.utils.notification.SendNotificationToUserAsyncTask
import com.example.advogo.utils.extensions.DataUtils
import com.example.advogo.utils.extensions.StringExtensions.fromUSADateStringToDate
import com.example.advogo.utils.extensions.StringExtensions.removeSpecialCharacters
import com.google.firebase.Timestamp
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessoCadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoCadastroBinding
    private lateinit var userName: String

    @Inject lateinit var processoRepository: IProcessoRepository
    @Inject lateinit var processoHistoricoRepository: IProcessoHistoricoRepository
    @Inject lateinit var processoTipoRepository: IProcessoTipoRepository
    @Inject lateinit var processoStatusRepository: IProcessoStatusRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository
    @Inject lateinit var clienteRepository: ClienteRepository

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

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessoCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar("Cadastro Processo", binding.toolbarProcessoCadastroActivity)

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

        binding.etAdv.setOnClickListener {
            advogadosDialog()
        }

        binding.etCliente.setOnClickListener {
            clientesDialog()
        }

        binding.etTipoProcesso.setOnClickListener {
            tiposProcessoDialog()
        }

        binding.etStatusProcesso.setOnClickListener {
            statusProcessoDialog()
        }

        binding.btnProcessoCadastro.setOnClickListener {
            saveProcesso()
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

    private fun tiposProcessoDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(processosTipos.isEmpty()) {
                val tiposProcessoDeferred = async { processoTipoRepository.obterProcessosTipos()!! }
                processosTipos = tiposProcessoDeferred.await()
            }

            val listDialog = object : ProcessoTiposDialog(
                this@ProcessoCadastroActivity,
                processosTipos,
            ) {
                override fun onItemSelected(item: ProcessoTipo, action: String) {
                    if (action == Constants.SELECIONAR) {
                        processosTipos.forEach {
                            it.selecionado = false
                        }

                        if (binding.etTipoProcesso.text.toString() != item.id) {
                            binding.etTipoProcesso.setText(item.tipo)
                            tipoProcessoSelecionado = item.id
                            processosTipos[processosTipos.indexOf(item)].selecionado = true
                        } else {
                            Toast.makeText(
                                this@ProcessoCadastroActivity,
                                "Tipo já selecionado! Favor escolher outro.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        binding.etTipoProcesso.text = null
                        tipoProcessoSelecionado = null
                        processosTipos[processosTipos.indexOf(item)].selecionado = false
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
                this@ProcessoCadastroActivity,
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
                                this@ProcessoCadastroActivity,
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

    private fun advogadosDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(advogados.isEmpty()) {
                val advogadosDeferred = async { advogadoRepository.obterAdvogados()!! }
                advogados = advogadosDeferred.await()
            }

            val listDialog = object : AdvogadosDialog(
                this@ProcessoCadastroActivity,
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
                                this@ProcessoCadastroActivity,
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
                this@ProcessoCadastroActivity,
                clientes as ArrayList<Cliente>,
                resources.getString(R.string.selecionarCliente)
            ) {
                override fun onItemSelected(cliente: Cliente, action: String) {
                    if (action == Constants.SELECIONAR) {
                        clientes.forEach {
                            it.selecionado = false
                        }

                        if (binding.etCliente.text.toString() != cliente.id) {
                            binding.etCliente.setText("${cliente.nome} (${cliente.cpf})")
                            clienteSelecionado = cliente.id
                            clientes[clientes.indexOf(cliente)].selecionado = true
                        } else {
                            Toast.makeText(
                                this@ProcessoCadastroActivity,
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

    private suspend fun salvarImagemProcesso(): String {
        return suspendCancellableCoroutine { continuation ->
            val numProcessoTratado =
                binding.etNumeroProcesso.text.toString().removeSpecialCharacters()

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "PROCESSO_${numProcessoTratado}_IMAGEM" + System.currentTimeMillis() + "."
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
                null
            }

            val processo = Processo(
                id = "",
                titulo = binding.etProcessoName.text.toString(),
                descricao = binding.etDescricao.text.toString(),
                numero = binding.etNumeroProcesso.text.toString(),
                tipo = tipoProcessoSelecionado,
                status = statusProcessoSelecionado,
                dataInicio = dataSelecionada,
                dataInicioTimestamp = Timestamp(dataSelecionada!!.fromUSADateStringToDate()),
                dataCriacao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                dataCriacaoTimestamp = Timestamp.now(),
                dataAlteracao = null,
                dataAlteracaoTimestamp = null,
                imagem = imageUrl,
                cliente = clienteSelecionado,
                advogado = advSelecionado
            )

            processoRepository.adicionarProcesso(
                processo,
                {
                    val historico = ProcessoHistorico(
                        obs = "PROCESSO CADASTRADO",
                        advogado = advSelecionado,
                        status = statusProcessoSelecionado,
                        tipo = tipoProcessoSelecionado,
                        processo = processo.numero,
                        data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        dataTimestamp = Timestamp.now()
                    )

                    processoHistoricoRepository.adicionarProcessoHistorico(
                        historico,
                        { null },
                        { null }
                    )

                    processoCadastroSuccess()
                },
                { processoCadastroFailure() }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDatePickerResult(ano: Int, mes: Int, dia: Int) {
        val retorno = DataUtils.onDatePickerResult(ano, mes, dia)

        dataSelecionada = retorno.dataUSA
        binding.etData.setText(retorno.dataBR)
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

    private fun processoCadastroSuccess() {
        hideProgressDialog()

        if(advSelecionado != getCurrentUserID()) {
            SendNotificationToUserAsyncTask(
                "Processo",
                "Novo processo cadastrado e desginado para você! Clique para ver seus processos.",
                advSelecionadoToken!!
            ).execute()
        }

        intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun processoCadastroFailure() {
        hideProgressDialog()

        Toast.makeText(
            this@ProcessoCadastroActivity,
            "Um erro ocorreu ao criar o processo.",
            Toast.LENGTH_SHORT
        ).show()
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