package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ActivityProcessoDetalheBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.Cliente
import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.repositories.AdvogadoRepository
import com.example.advogo.repositories.ProcessoRepository
import com.example.advogo.utils.Constants
import com.example.projmgr.dialogs.AdvogadosDialog
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ProcessoDetalheActivity : BaseActivity() {
    @Inject lateinit var processoRepository: ProcessoRepository
    @Inject lateinit var advogadoRepository: AdvogadoRepository

    private lateinit var binding: ActivityProcessoDetalheBinding
    private lateinit var processoDetalhes: Processo

    private var advogados: List<Advogado> = ArrayList()
    private var dataSelecionada: String? = null

    private var imagemSelecionadaURI: Uri? = null
    private var imagemSelecionadaURL: String? = null

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessoDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        obterIntentDados()

        setProcessoToUI(processoDetalhes)
        advogados = carregarAdvogados()

        binding.tvSelectData.setOnClickListener {
            showDataPicker() { ano, mes, dia ->
                onDatePickerResult(ano, mes, dia)
            }
        }

        binding.btnProcessoCadastro.setOnClickListener {
            if(imagemSelecionadaURI != null) {
                salvarImagemProcesso()
            } else {
                saveProcesso()
            }
        }

        binding.ivProcessoImage.setOnClickListener {
            chooseImage(this@ProcessoDetalheActivity, resultLauncher)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagemSelecionadaURI = result.data!!.data!!

                try {
                    Glide
                        .with(this@ProcessoDetalheActivity)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_processo_delete, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_deletar_processo -> {
                alertDialogDeletarProcesso(processoDetalhes.id!!)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
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
                    this@ProcessoDetalheActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                //TODO("hideProgressDialog()")
            }
    }

    private fun saveProcesso() {
        //TODO("showProgressDialog("Please wait...")")

        val processo = Processo(
            id = processoDetalhes.id,
            descricao = (if (binding.etDescricao.text.toString() != processoDetalhes.descricao) binding.etDescricao.text.toString() else processoDetalhes.descricao),
            numero = (if (binding.etNumeroProcesso.text.toString() != processoDetalhes.numero) binding.etNumeroProcesso.text.toString() else processoDetalhes.descricao),
            tipo = (if (binding.etTipo.text.toString() != processoDetalhes.tipo) binding.etTipo.text.toString() else processoDetalhes.tipo),
            status = (if (binding.etStatus.text.toString() != processoDetalhes.status) binding.etStatus.text.toString() else processoDetalhes.status),
            data = processoDetalhes.data,
            imagem = (if (imagemSelecionadaURL!!.isNotEmpty() && imagemSelecionadaURL != processoDetalhes.imagem) imagemSelecionadaURL else processoDetalhes.imagem),
            cliente = (if (binding.etCliente.text.toString() != processoDetalhes.cliente.toString()) binding.etCliente.text.toString() else processoDetalhes.cliente.toString()),
            advogado = (if (binding.etAdv.text.toString() != processoDetalhes.advogado) binding.etAdv.text.toString() else processoDetalhes.advogado),
        )

        processoRepository.AdicionarProcesso(
            processo,
            { atualizarProcessoSuccess() },
            { atualizarProcessoFailure() }
        )
    }

    private fun deletarProcesso() {
        processoRepository.DeletarProcesso(
            processoDetalhes.id!!,
            { deletarProcessoSuccess() },
            { deletarProcessoFailure() }
        )
    }

    private fun advogadosDialog() {
        if(advogados.isEmpty()) {
            advogados = carregarAdvogados()
        }
        
        val listDialog = object : AdvogadosDialog(
            this@ProcessoDetalheActivity,
            advogados as ArrayList<Advogado>,
            resources.getString(R.string.selecionarAdvogado)
        ) {
            override fun onItemSelected(adv: Advogado, action: String) {
                if (action == Constants.SELECIONAR) {
                    if (processoDetalhes.advogado != adv.id) {
                        processoDetalhes.advogado = adv.id                        
                        advogados[advogados.indexOf(adv)].selecionado = true
                    } else {
                        //TODO("ADVOGADO JÁ SELECIONADO")
                    }
                } else {
                    processoDetalhes.advogado = null
                    advogados[advogados.indexOf(adv)].selecionado = false
                }

                setAdvogadosToUI()
            }
        }
        
        listDialog.show()
    }



    private fun setAdvogadosToUI() {
//        val cardAssignedMembersList =
//            boardDetails.taskList!![taskListPosition].cards!![cardPosition].assignedTo
//
//        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
//
//        for (i in membersDetailList.indices) {
//            for (j in cardAssignedMembersList) {
//                if (membersDetailList[i].id == j) {
//                    val selectedMember = SelectedMembers(
//                        membersDetailList[i].id,
//                        membersDetailList[i].image!!
//                    )
//
//                    selectedMembersList.add(selectedMember)
//                }
//            }
//        }
//
//        if (selectedMembersList.size > 0) {
//            selectedMembersList.add(SelectedMembers("", ""))
//
//            binding.tvSelectMembers.visibility = View.GONE
//            binding.tvSelectMembers.visibility = View.VISIBLE
//
//            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this@CardDetailsActivity, 6)
//
//            val adapter = CardMembersListItemsAdapter(this@CardDetailsActivity, selectedMembersList, true)
//            binding.rvSelectedMembersList.adapter = adapter
//            adapter.setOnItemClickListener(object :
//                CardMembersListItemsAdapter.OnItemClickListener {
//                override fun onClick() {
//                    membersListDialog()
//                }
//            })
//        } else {
//            binding.tvSelectMembers.visibility = View.VISIBLE
//            binding.rvSelectedMembersList.visibility = View.GONE
//        }
    }

    private fun setProcessoToUI(processo: Processo) {
        binding.etProcessoName.setText(processo.titulo)
        binding.etDescricao.setText(processo.descricao)
        binding.etTipo.setText(processo.tipoObj?.tipo)
        binding.etStatus.setText(processo.statusObj?.status)
        binding.etData.setText(processo.data)
        binding.etNumeroProcesso.setText(processo.numero)
        binding.etAdv.setText(processo.advogadoObj?.nome)
        binding.etCliente.setText(processo.clienteObj?.nome)

        dataSelecionada = processoDetalhes.data
        if(!dataSelecionada.isNullOrEmpty()) {
            val fromFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val toFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val fromDate = fromFormat.parse(dataSelecionada)
            val selectedDate = toFormat.format(fromDate)
            binding.tvSelectData.text = selectedDate
        }
    }

    private fun obterIntentDados() {
        if (intent.hasExtra(Constants.PROCESSO_PARAM)) {
            processoDetalhes = intent.getParcelableExtra<Processo>(Constants.PROCESSO_PARAM)!!
        }
    }

    private fun onDatePickerResult(year: Int, month: Int, day: Int) {
        val sDayOfMonth = if (day < 10) "0$day" else "$day"
        val sMonthOfYear = if ((month + 1) < 10) "0${month + 1}" else "${month + 1}"

        val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
        binding.tvSelectData.text = selectedDate
//
//        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
//        val theDate = sdf.parse(selectedDate)
//        dataSelecionada = theDate!!.toLocaleString()
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

    private fun carregarAdvogados(): List<Advogado> {
        var retorno: List<Advogado> = ArrayList()

        advogadoRepository.ObterAdvogados(
            { lista -> retorno = lista },
            { null } //TODO("Implementar")
        )

        return retorno
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProcessoDetalheActivity)

        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            //actionBar.title = boardDetails.taskList?.get(taskListPosition)!!.cards!![cardPosition].name
        }

        binding.toolbarProcessoDetalheActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun atualizarProcessoSuccess() {
        //TODO("hideProgressDialog()")
        setResult(RESULT_OK)
        finish()
    }

    private fun atualizarProcessoFailure() {
        //TODO("hideProgressDialog()")
    }

    private fun deletarProcessoSuccess() {
        //TODO("hideProgressDialog()")
        finish()
    }

    private fun deletarProcessoFailure() {
        //TODO("hideProgressDialog()")
    }
}