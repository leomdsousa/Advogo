package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosStatusAdapter
import com.example.advogo.adapters.ProcessosTiposAdapter
import com.example.advogo.databinding.ActivityProcessoCadastroBinding
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.example.projmgr.dialogs.AdvogadosDialog
import com.example.projmgr.dialogs.ClientesDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessoCadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoCadastroBinding
    private lateinit var userName: String

    @Inject lateinit var processoRepository: IProcessoRepository
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

        binding.etAdv.setOnClickListener {
            advogadosDialog()
        }

        binding.etCliente.setOnClickListener {
            clientesDialog()
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

    private fun advogadosDialog() {
        CoroutineScope(Dispatchers.Main).launch {
            if(advogados.isEmpty()) {
                val advogadosDeferred = async { advogadoRepository.ObterAdvogados()!! }
                advogados = advogadosDeferred.await()
            }

            val listDialog = object : AdvogadosDialog(
                this@ProcessoCadastroActivity,
                advogados as ArrayList<Advogado>,
                resources.getString(R.string.selecionarAdvogado)
            ) {
                override fun onItemSelected(adv: Advogado, action: String) {
                    if (action == Constants.SELECIONAR) {
                        if (binding.etAdv.text.toString() != adv.id) {
                            binding.etAdv.setText("${adv.nome} (${adv.oab})")
                            advSelecionado = adv.id
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
                val clientesDeferred = async { clienteRepository.ObterClientes()!! }
                clientes = clientesDeferred.await()
            }

            val listDialog = object : ClientesDialog(
                this@ProcessoCadastroActivity,
                clientes as ArrayList<Cliente>,
                resources.getString(R.string.selecionarCliente)
            ) {
                override fun onItemSelected(cliente: Cliente, action: String) {
                    if (action == Constants.SELECIONAR) {
                        if (binding.etCliente.text.toString() != cliente.id) {
                            binding.etAdv.setText("${cliente.nome} (${cliente.cpf})")
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

    private fun setupSpinners() {
        setupSpinnerTiposProcesso()
        setupSpinnerStatusProcesso()

    }

    private fun setupSpinnerStatusProcesso() {
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatusProcesso)

        CoroutineScope(Dispatchers.Main).launch {
            val processosStatusDeferred = async { processoStatusRepository.ObterProcessoStatus() }
            processosStatus = processosStatusDeferred.await()!!

            val adapter = ProcessosStatusAdapter(this@ProcessoCadastroActivity, processosStatus)
            spinnerStatus.adapter = adapter
        }

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as? String
                selectedItem?.let {
                    binding.autoTvTipoProcesso.setText(it)
                    spinnerStatus.setSelection(id.toInt())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Nada selecionado
            }
        }
    }

    private fun setupSpinnerTiposProcesso() {
        val spinnerTipos = findViewById<Spinner>(R.id.spinnerTipoProcesso)

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
                    binding.autoTvTipoProcesso.setText(it)
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
            id = "",
            descricao = binding.etDescricao.text.toString(),
            numero = binding.etNumeroProcesso.text.toString(),
            tipo = binding.autoTvTipoProcesso.text.toString(),
            status = binding.autoTvStatusProcesso.text.toString(),
            data = dataSelecionada,
            imagem = imagemSelecionadaURL,
            cliente = clienteSelecionado,
            advogado = advSelecionado
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