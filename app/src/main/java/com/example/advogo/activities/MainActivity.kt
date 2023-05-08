package com.example.advogo.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.adapters.ProcessosAdapter
import com.example.advogo.databinding.ActivityMainBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.Processo
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.repositories.IProcessoRepository
import com.example.advogo.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    @Inject lateinit var _processoRepository: IProcessoRepository
    @Inject lateinit var _advRepository: IAdvogadoRepository
    @Inject lateinit var _diligenciaRepository: IDiligenciaRepository

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var advNome: String
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()

        binding.navView.setNavigationItemSelectedListener(this)

        sharedPreferences =
            this.getSharedPreferences(Constants.ADVOGO_PREFERENCES, Context.MODE_PRIVATE)

        _processoRepository.ObterProcessos(
            { processos -> setProcessosToUI(processos as ArrayList<Processo>) },
            { null } //TODO("Implementar")
        )

//        val tokenUpdated = sharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)
//
//        if (tokenUpdated) {
//            showProgressDialog(resources.getString(R.string.please_wait))
//            FirestoreService().loadUserData(this@MainActivity, true)
//        } else {
//            FirebaseMessaging.getInstance()
//                .token
//                .addOnSuccessListener(this@MainActivity) { instanceIdResult ->
//                    updateFCMToken(instanceIdResult, this@MainActivity)
//                }
//        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_PERFIL_ACTIVITY)) {
                    _advRepository.ObterAdvogado(
                        getCurrentUserID(),
                        { adv -> setNavigationAdvDetalhes(adv) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                } else if (result.data!!.hasExtra(Constants.FROM_PROCESSO_CADASTRO_ACTIVITY)) {
                    _processoRepository.ObterProcessos(
                        { lista -> setProcessosToUI(lista!! as ArrayList<Processo>) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }

        binding.appBarMain.fabProcessoCadastro.setOnClickListener {
            val intent = Intent(this@MainActivity, ProcessoCadastroActivity::class.java)
            intent.putExtra(Constants.ADV_NOME_PARAM, advNome)
            intent.putExtra(Constants.FROM_PROCESSO_CADASTRO_ACTIVITY, Constants.FROM_PROCESSO_CADASTRO_ACTIVITY)
            resultLauncher.launch(intent)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.navPerfil -> {
                val intent = Intent(this@MainActivity, PerfilActivity::class.java)
                intent.putExtra(Constants.FROM_PERFIL_ACTIVITY, Constants.FROM_PERFIL_ACTIVITY)
                resultLauncher.launch(intent)
            }
            R.id.navDeslogar -> {
                FirebaseAuth.getInstance().signOut()
                sharedPreferences.edit().clear().apply()

                val intent = Intent(this@MainActivity, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setProcessosToUI(lista: ArrayList<Processo>) {
        //TODO("hideProgressDialog()")

        if(lista.size > 0) {
            binding.appBarMain.contentMain

            binding.appBarMain.contentMain.rvBoardsList.visibility = View.VISIBLE
            binding.appBarMain.contentMain.tvNoBoardsAvailable.visibility = View.GONE

            binding.appBarMain.contentMain.rvBoardsList.layoutManager = LinearLayoutManager(this@MainActivity)
            binding.appBarMain.contentMain.rvBoardsList.setHasFixedSize(true)

            val adapter = ProcessosAdapter(this@MainActivity, lista)
            binding.appBarMain.contentMain.rvBoardsList.adapter = adapter

            adapter.setOnItemClickListener(object :
                ProcessosAdapter.OnItemClickListener {
                override fun onClick(model: Processo, position: Int) {
                    val intent = Intent(this@MainActivity, ProcessoDetalheActivity::class.java)
                    intent.putExtra(Constants.PROCESSO_ID_PARAM, model.id)
                    startActivity(intent)
                }
            })

        } else {
            binding.appBarMain.contentMain.rvBoardsList.visibility = View.GONE
            binding.appBarMain.contentMain.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    fun setNavigationAdvDetalhes(adv: Advogado) {
        val headerView = binding.navView.getHeaderView(0)
        val navUserImage = headerView.findViewById<ImageView>(R.id.ivUserImageNav)

        advNome = adv.nome!!

        Glide
            .with(this@MainActivity)
            .load(adv.imagem)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        val navUserName = headerView.findViewById<TextView>(R.id.tvUsernameNav)
        navUserName.text = adv.nome

//        if (readBoardsList) {
//            showProgressDialog(resources.getString(R.string.please_wait))
//            FirestoreService().getBoardsList(this@MainActivity)
//        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.appBarMain.toolbarMain)
        binding.appBarMain.toolbarMain.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        binding.appBarMain.toolbarMain.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
}