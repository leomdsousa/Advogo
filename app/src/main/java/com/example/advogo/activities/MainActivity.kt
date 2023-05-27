package com.example.advogo.activities

import TabsAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ActivityMainBinding
import com.example.advogo.fragments.ClienteFragment
import com.example.advogo.fragments.DiligenciasFragment
import com.example.advogo.fragments.ProcessosFragment
import com.example.advogo.models.Advogado
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.repositories.IProcessoRepository
import com.example.advogo.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    @Inject lateinit var processoRepository: IProcessoRepository
    @Inject lateinit var advRepository: IAdvogadoRepository
    @Inject lateinit var diligenciaRepository: IDiligenciaRepository

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var advNome: String
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupTabsLayout()

        advRepository.ObterAdvogado(
            getCurrentUserID(),
            { adv -> setNavigationAdvDetalhes(adv) },
            { ex -> null } //TODO("Imlementar OnFailure")
        )

        binding.navView.setNavigationItemSelectedListener(this)

        sharedPreferences =
            this.getSharedPreferences(Constants.ADVOGO_PREFERENCES, Context.MODE_PRIVATE)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_PERFIL_ACTIVITY)) {
                    advRepository.ObterAdvogado(
                        getCurrentUserID(),
                        { adv -> setNavigationAdvDetalhes(adv) },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                }
            }
        }
    }

    private fun setupTabsLayout() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val adapter = TabsAdapter(this)
        adapter.addFragment(ClienteFragment(), "Clientes")
        adapter.addFragment(ProcessosFragment(), "Processos")
        //adapter.addFragment(DiligenciasFragment(), "Eventos")
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
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

    private fun setNavigationAdvDetalhes(adv: Advogado) {
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
        navUserName.text = advNome
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