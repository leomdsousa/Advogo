package com.example.advogo.activities

import TabsAdapter
import android.app.Activity
import android.content.Intent
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
import com.example.advogo.R
import com.example.advogo.adapters.DiligenciasStatusAdapter
import com.example.advogo.adapters.DiligenciasTiposAdapter
import com.example.advogo.databinding.ActivityDiligenciaDetalheBinding
import com.example.advogo.fragments.*
import com.example.advogo.models.*
import com.example.advogo.repositories.*
import com.example.advogo.utils.Constants
import com.example.advogo.utils.SendNotificationToUserAsyncTask
import com.example.advogo.dialogs.AdvogadosDialog
import com.example.advogo.dialogs.ProcessosDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiligenciaDetalheBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        obterIntentDados()
        setupActionBar("Detalhe Diligência", binding.toolbarDiligenciaDetalhe)
        setupTabsLayout()
    }

    private fun setupTabsLayout() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val adapter = TabsAdapter(this)
        adapter.addFragment(DiligenciaDetalheFragment(), "Dados")
        adapter.addFragment(DiligenciaHistoricoFragment(), "Histórico")
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customView = LayoutInflater.from(this@DiligenciaDetalheActivity)
                .inflate(R.layout.item_tab_layout, tabLayout, false)

            val tabIcon: ImageView = customView.findViewById(R.id.tab_icon)
            val tabTitle: TextView = customView.findViewById(R.id.tab_title)

            tabIcon.setImageResource(getTabIcon(position))
            tabTitle.text = adapter.getTabTitle(position)

            tab.customView = customView
        }.attach()
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
                alertDialogDeletarDiligencia("${diligenciaDetalhes.descricao!!} (Processo ${diligenciaDetalhes.processoObj?.numero})")
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
        diligenciaRepository.deletarDiligencia(
            diligenciaDetalhes.id,
            { deletarDiligenciaSuccess() },
            { deletarDiligenciaFailure() }
        )
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

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            0 -> R.drawable.ic_baseline_app_registration_24
            1 -> R.drawable.ic_baseline_timeline_24
            else -> 0
        }
    }
}