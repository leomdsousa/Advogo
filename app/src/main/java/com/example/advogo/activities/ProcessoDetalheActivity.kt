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

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessoDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        obterIntentDados()
        setupActionBar("Detalhe Processo", binding.toolbarProcessoDetalheActivity)
        setupTabsLayout()

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_PROCESSO_ACTIVITY)) {
                    processoRepository.ObterProcessos(
                        { lista ->
//                           setProcessosToUI(lista!! as ArrayList<Processo>)
//                           hideProgressDialog()
                        },
                        { hideProgressDialog() }
                    )
                }
            }
        }
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
        adapter.addFragment(ProcessoAndamentoFragment(), "Andamentos")
        adapter.addFragment(ProcessoAnexoFragment(), "Anexos")
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customView = LayoutInflater.from(this@ProcessoDetalheActivity)
                .inflate(R.layout.item_tab_layout, tabLayout, false)

            val tabIcon: ImageView = customView.findViewById(R.id.tab_icon)
            val tabTitle: TextView = customView.findViewById(R.id.tab_title)

            tabIcon.setImageResource(getTabIcon(position))
            tabTitle.text = adapter.getTabTitle(position)

            tab.customView = customView
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
        hideProgressDialog()

        intent.putExtra(Constants.FROM_PROCESSO_ACTIVITY, Constants.FROM_PROCESSO_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun deletarProcessoFailure() {
        hideProgressDialog()
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            0 -> R.drawable.ic_baseline_app_registration_24
            1 -> R.drawable.ic_baseline_timeline_24
            2 -> R.drawable.ic_baseline_attachment_24
            else -> 0
        }
    }
}