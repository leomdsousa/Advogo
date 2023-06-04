package com.example.advogo.activities

import TabsAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosStatusAdapter
import com.example.advogo.adapters.ProcessosTiposAdapter
import com.example.advogo.databinding.ActivityProcessoDetalheBinding
import com.example.advogo.fragments.*
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.example.advogo.utils.ProcessMaskTextWatcher
import com.example.projmgr.dialogs.AdvogadosDialog
import com.example.projmgr.dialogs.ClientesDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
class ProcessoDetalheActivity : BaseActivity() {
    private lateinit var binding: ActivityProcessoDetalheBinding
    private lateinit var processoDetalhes: Processo

    @Inject lateinit var processoRepository: ProcessoRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository
    @Inject lateinit var clienteRepository: ClienteRepository
    @Inject lateinit var processoTipoRepository: IProcessoTipoRepository
    @Inject lateinit var processoStatusRepository: IProcessoStatusRepository

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessoDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        obterIntentDados()
        setupActionBar("Detalhe Processo", binding.toolbarProcessoDetalheActivity)
        setupTabsLayout()
    }

    private fun obterIntentDados() {
        if (intent.hasExtra(Constants.PROCESSO_PARAM)) {
            processoDetalhes = intent.getParcelableExtra<Processo>(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun setupTabsLayout() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val adapter = TabsAdapter(this)
        adapter.addFragment(ProcessoDetalheFragment(), "Dados")
        adapter.addFragment(ProcessoAnexoFragment(), "Anexos")
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_processo_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_deletar_processo -> {
                alertDialogDeletarProcesso("${processoDetalhes.numero.toString()} (${processoDetalhes.titulo})")
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun deletarProcesso() {
        processoRepository.DeletarProcesso(
            processoDetalhes.id,
            { deletarProcessoSuccess() },
            { deletarProcessoFailure() }
        )
    }

    private fun alertDialogDeletarProcesso(numeroProcesso: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.atencao))
        builder.setMessage(
            resources.getString(
                R.string.confirmacaoDeletarProcesso,
                numeroProcesso
            )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.sim)) { dialogInterface, which ->
            dialogInterface.dismiss()
            deletarProcesso()
        }

        builder.setNegativeButton(resources.getString(R.string.nao)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun deletarProcessoSuccess() {
        //TODO("hideProgressDialog()")
        intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun deletarProcessoFailure() {
        //TODO("hideProgressDialog()")
    }
}

//@AndroidEntryPoint
//class ProcessoDetalheActivity : BaseActivity() {
//    private lateinit var binding: ActivityProcessoDetalheBinding
//    private lateinit var processoDetalhes: Processo
//
//    @Inject lateinit var processoRepository: ProcessoRepository
//    @Inject lateinit var advogadoRepository: AdvogadoRepository
//    @Inject lateinit var clienteRepository: ClienteRepository
//    @Inject lateinit var processoTipoRepository: IProcessoTipoRepository
//    @Inject lateinit var processoStatusRepository: IProcessoStatusRepository
//
//    private var advogados: List<Advogado> = ArrayList()
//    private var clientes: List<Cliente> = ArrayList()
//    private var processosTipos: List<ProcessoTipo> = ArrayList()
//    private var processosStatus: List<ProcessoStatus> = ArrayList()
//
//    private var dataSelecionada: String? = null
//    private var clienteSelecionado: String? = null
//    private var advSelecionado: String? = null
//    private var tipoProcessoSelecionado: String? = null
//    private var statusProcessoSelecionado: String? = null
//
//    private var imagemSelecionadaURI: Uri? = null
//    private var imagemSelecionadaURL: String? = null
//
//    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityProcessoDetalheBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        obterIntentDados()
//        setupActionBar()
//        setupSpinners()
//        setProcessoToUI(processoDetalhes)
//
//        binding.etNumeroProcesso.addTextChangedListener(ProcessMaskTextWatcher(binding.etNumeroProcesso))
//
//        binding.tvSelectData.setOnClickListener {
//            showDataPicker() { ano, mes, dia ->
//                onDatePickerResult(ano, mes, dia)
//            }
//        }
//
//        binding.btnProcessoCadastro.setOnClickListener {
//            if(imagemSelecionadaURI != null) {
//                salvarImagemProcesso()
//            } else {
//                saveProcesso()
//            }
//        }
//
//        binding.ivProcessoImage.setOnClickListener {
//            chooseImage(this@ProcessoDetalheActivity, resultLauncher)
//        }
//
//        binding.etAdv.setOnClickListener {
//            advogadosDialog()
//        }
//
//        binding.etCliente.setOnClickListener {
//            clientesDialog()
//        }
//
//        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                imagemSelecionadaURI = result.data!!.data!!
//
//                try {
//                    Glide
//                        .with(this@ProcessoDetalheActivity)
//                        .load(Uri.parse(imagemSelecionadaURI.toString()))
//                        .centerCrop()
//                        .placeholder(R.drawable.ic_user_place_holder)
//                        .into(binding.ivProcessoImage)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }
//
//    private fun setupSpinners() {
//        setupSpinnerTiposProcesso()
//        setupSpinnerStatusProcesso()
//
//    }
//
//    private fun setupSpinnerStatusProcesso() {
//        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatusProcesso)
//
//        CoroutineScope(Dispatchers.Main).launch {
//            val processosStatusDeferred = async { processoStatusRepository.ObterProcessoStatus() }
//            processosStatus = processosStatusDeferred.await()!!
//            (processosStatus as MutableList<ProcessoStatus>).add(0, ProcessoStatus(status = "Selecione"))
//
//            val adapter = ProcessosStatusAdapter(this@ProcessoDetalheActivity, processosStatus)
//            spinnerStatus.adapter = adapter
//        }
//
//        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedItem = parent?.getItemAtPosition(position) as? String
//                selectedItem?.let {
//                    statusProcessoSelecionado = selectedItem
//                    spinnerStatus.setSelection(id.toInt())
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // Nada selecionado
//            }
//        }
//    }
//
//    private fun setupSpinnerTiposProcesso() {
//        val spinnerTipos = findViewById<Spinner>(R.id.spinnerTipoProcesso)
//
//        CoroutineScope(Dispatchers.Main).launch {
//            val processosTiposDeferred = async { processoTipoRepository.ObterProcessosTipos() }
//            processosTipos = processosTiposDeferred.await()!!
//            (processosTipos as MutableList<ProcessoTipo>).add(0, ProcessoTipo(tipo = "Selecione"))
//
//            val adapter = ProcessosTiposAdapter(this@ProcessoDetalheActivity, processosTipos)
//            spinnerTipos.adapter = adapter
//        }
//
//        spinnerTipos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedItem = parent?.getItemAtPosition(position) as? String
//                selectedItem?.let {
//                    tipoProcessoSelecionado = selectedItem
//                    spinnerTipos.setSelection(id.toInt())
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                // Nada selecionado
//            }
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_processo_delete, menu)
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                onBackPressed()
//                return true
//            }
//            R.id.action_deletar_processo -> {
//                alertDialogDeletarProcesso("${processoDetalhes.numero.toString()} (${processoDetalhes.titulo})")
//                return true
//            }
//        }
//
//        return super.onOptionsItemSelected(item)
//    }
//
//    private fun salvarImagemProcesso() {
//        //TODO("showProgressDialog("Please wait...")")
//
//        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
//            "PROCESSO_${processoDetalhes.numero}_IMAGEM" + System.currentTimeMillis() + "."
//                    + getFileExtension(imagemSelecionadaURI!!)
//        )
//
//        sRef.putFile(imagemSelecionadaURI!!)
//            .addOnSuccessListener { taskSnapshot ->
//                taskSnapshot.metadata!!.reference!!.downloadUrl
//                    .addOnSuccessListener { uri ->
//                        imagemSelecionadaURL = uri.toString()
//                        saveProcesso()
//                    }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(
//                    this@ProcessoDetalheActivity,
//                    exception.message,
//                    Toast.LENGTH_LONG
//                ).show()
//
//                //TODO("hideProgressDialog()")
//            }
//    }
//
//    private fun saveProcesso() {
//        if(!validarFormulario()) {
//            return
//        }
//
//        //TODO("showProgressDialog("Please wait...")")
//
//        val processo = Processo(
//            id = processoDetalhes.id,
//            descricao = (if (binding.etDescricao.text.toString() != processoDetalhes.descricao) binding.etDescricao.text.toString() else processoDetalhes.descricao),
//            numero = (if (binding.etNumeroProcesso.text.toString() != processoDetalhes.numero) binding.etNumeroProcesso.text.toString() else processoDetalhes.descricao),
//            tipo = (if (tipoProcessoSelecionado != processoDetalhes.tipo) tipoProcessoSelecionado else processoDetalhes.tipo),
//            status = (if (statusProcessoSelecionado != processoDetalhes.status) statusProcessoSelecionado else processoDetalhes.status),
//            data = processoDetalhes.data,
//            imagem = (if (imagemSelecionadaURL != processoDetalhes.imagem) imagemSelecionadaURL else processoDetalhes.imagem),
//            cliente = (if (clienteSelecionado != processoDetalhes.cliente.toString()) clienteSelecionado else processoDetalhes.cliente.toString()),
//            advogado = (if (advSelecionado != processoDetalhes.advogado) advSelecionado else processoDetalhes.advogado),
//        )
//
//        processoRepository.AdicionarProcesso(
//            processo,
//            { atualizarProcessoSuccess() },
//            { atualizarProcessoFailure() }
//        )
//    }
//
//    private fun deletarProcesso() {
//        processoRepository.DeletarProcesso(
//            processoDetalhes.id,
//            { deletarProcessoSuccess() },
//            { deletarProcessoFailure() }
//        )
//    }
//
//    private fun advogadosDialog() {
//        CoroutineScope(Dispatchers.Main).launch {
//            if(advogados.isEmpty()) {
//                val advogadosDeferred = async { advogadoRepository.ObterAdvogados()!! }
//                advogados = advogadosDeferred.await()
//            }
//
//            val listDialog = object : AdvogadosDialog(
//                this@ProcessoDetalheActivity,
//                advogados as ArrayList<Advogado>,
//                resources.getString(R.string.selecionarAdvogado)
//            ) {
//                override fun onItemSelected(adv: Advogado, action: String) {
//                    if (action == Constants.SELECIONAR) {
//                        if (binding.etAdv.text.toString() != adv.id) {
//                            binding.etAdv.setText("${adv.nome} (${adv.oab})")
//                            advSelecionado = adv.id
//                            advogados[advogados.indexOf(adv)].selecionado = true
//                        } else {
//                            Toast.makeText(
//                                this@ProcessoDetalheActivity,
//                                "Advogado já selecionado! Favor escolher outro.",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    } else {
//                        binding.etAdv.text = null
//                        advSelecionado = null
//                        advogados[advogados.indexOf(adv)].selecionado = false
//                    }
//                }
//            }
//
//            listDialog.show()
//        }
//    }
//
//    private fun clientesDialog() {
//        CoroutineScope(Dispatchers.Main).launch {
//            if(clientes.isEmpty()) {
//                val clientesDeferred = async { clienteRepository.ObterClientes()!! }
//                clientes = clientesDeferred.await()
//            }
//
//            val listDialog = object : ClientesDialog(
//                this@ProcessoDetalheActivity,
//                clientes as ArrayList<Cliente>,
//                resources.getString(R.string.selecionarCliente)
//            ) {
//                override fun onItemSelected(cliente: Cliente, action: String) {
//                    if (action == Constants.SELECIONAR) {
//                        if (binding.etCliente.text.toString() != cliente.id) {
//                            binding.etCliente.setText("${cliente.nome} (${cliente.cpf})")
//                            clienteSelecionado = cliente.id
//                            clientes[clientes.indexOf(cliente)].selecionado = true
//                        } else {
//                            Toast.makeText(
//                                this@ProcessoDetalheActivity,
//                                "Cliente já selecionado! Favor escolher outro.",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    } else {
//                        binding.etCliente.text = null
//                        clienteSelecionado = null
//                        clientes[clientes.indexOf(cliente)].selecionado = false
//                    }
//                }
//            }
//
//            listDialog.show()
//        }
//    }
//
//    private fun setProcessoToUI(processo: Processo) {
//        binding.etProcessoName.setText(processo.titulo)
//        binding.etDescricao.setText(processo.descricao)
//        binding.spinnerTipoProcesso.setSelection(processosTipos.indexOf(processo.tipoObj))
//        binding.spinnerStatusProcesso.setSelection(processosStatus.indexOf(processo.statusObj))
//        binding.etNumeroProcesso.setText(processo.numero)
//        binding.etAdv.setText(processo.advogadoObj?.nome)
//        binding.etCliente.setText(processo.clienteObj?.nome)
//
//        advSelecionado = processoDetalhes.advogado
//        clienteSelecionado = processoDetalhes.cliente
//        tipoProcessoSelecionado = processoDetalhes.tipo
//        statusProcessoSelecionado = processoDetalhes.status
//        dataSelecionada = processoDetalhes.data
//
//        if(!dataSelecionada.isNullOrEmpty()) {
//            val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
//            val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
//            val fromDate = fromFormat.parse(dataSelecionada)
//            val selectedDate = toFormat.format(fromDate)
//            binding.etData.setText(selectedDate)
//        }
//    }
//
//    private fun validarFormulario(): Boolean {
//        var validado = true
//
//        if (TextUtils.isEmpty(binding.etProcessoName.text.toString())) {
//            binding.etProcessoName.error = "Obrigatório"
//            binding.etProcessoName.requestFocus()
//            validado = false
//        }
//
//        if (TextUtils.isEmpty(binding.etNumeroProcesso.text.toString())) {
//            binding.etNumeroProcesso.error = "Obrigatório"
//            binding.etNumeroProcesso.requestFocus()
//            validado = false
//        }
//
//        if (TextUtils.isEmpty(binding.etDescricao.text.toString())) {
//            binding.etDescricao.error = "Obrigatório"
//            binding.etDescricao.requestFocus()
//            validado = false
//        }
//
//        if (TextUtils.isEmpty(tipoProcessoSelecionado)) {
//            validado = false
//        }
//
//        if (TextUtils.isEmpty(statusProcessoSelecionado)) {
//            validado = false
//        }
//
//        if (TextUtils.isEmpty(dataSelecionada)) {
//            binding.etData.error = "Obrigatório"
//            binding.etData.requestFocus()
//            validado = false
//        }
//
//        if (TextUtils.isEmpty(clienteSelecionado)) {
//            binding.etCliente.error = "Obrigatório"
//            binding.etCliente.requestFocus()
//            validado = false
//        }
//
//        if (TextUtils.isEmpty(advSelecionado)) {
//            binding.etAdv.error = "Obrigatório"
//            binding.etAdv.requestFocus()
//            validado = false
//        }
//
//        return validado
//    }
//
//    private fun obterIntentDados() {
//        if (intent.hasExtra(Constants.PROCESSO_PARAM)) {
//            processoDetalhes = intent.getParcelableExtra<Processo>(Constants.PROCESSO_PARAM)!!
//        }
//    }
//
//    private fun onDatePickerResult(year: Int, month: Int, day: Int) {
//        val sDayOfMonth = if (day < 10) "0$day" else "$day"
//        val sMonthOfYear = if ((month + 1) < 10) "0${month + 1}" else "${month + 1}"
//
//        val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
//        binding.tvSelectData.text = selectedDate
//    }
//
//    private fun alertDialogDeletarProcesso(numeroProcesso: String) {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle(resources.getString(R.string.atencao))
//        builder.setMessage(
//            resources.getString(
//                R.string.confirmacaoDeletarProcesso,
//                numeroProcesso
//            )
//        )
//        builder.setIcon(android.R.drawable.ic_dialog_alert)
//
//        builder.setPositiveButton(resources.getString(R.string.sim)) { dialogInterface, which ->
//            dialogInterface.dismiss()
//            deletarProcesso()
//        }
//
//        builder.setNegativeButton(resources.getString(R.string.nao)) { dialogInterface, which ->
//            dialogInterface.dismiss()
//        }
//
//        val alertDialog: AlertDialog = builder.create()
//        alertDialog.setCancelable(false)
//        alertDialog.show()
//    }
//
//    private fun setupActionBar() {
//        setSupportActionBar(binding.toolbarProcessoDetalheActivity)
//
//        val actionBar = supportActionBar
//        if(actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true)
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
//            actionBar.title = "Detalhe Processo"
//        }
//
//        binding.toolbarProcessoDetalheActivity.setNavigationOnClickListener { onBackPressed() }
//    }
//
//    private fun atualizarProcessoSuccess() {
//        //TODO("hideProgressDialog()")
//        intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
//        setResult(Activity.RESULT_OK, intent)
//        finish()
//    }
//
//    private fun atualizarProcessoFailure() {
//        //TODO("hideProgressDialog()")
//    }
//
//    private fun deletarProcessoSuccess() {
//        //TODO("hideProgressDialog()")
//        intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
//        setResult(Activity.RESULT_OK, intent)
//        finish()
//    }
//
//    private fun deletarProcessoFailure() {
//        //TODO("hideProgressDialog()")
//    }
//}