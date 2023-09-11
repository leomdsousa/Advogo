package com.example.advogo.activities

import TabsAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
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
import com.example.advogo.utils.UserUtils.getCurrentUserID
import com.example.advogo.utils.constants.Constants
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener
{
    @Inject lateinit var processoRepository: IProcessoRepository
    @Inject lateinit var advRepository: IAdvogadoRepository
    @Inject lateinit var diligenciaRepository: IDiligenciaRepository

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var adv: Advogado
    private lateinit var advNome: String
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupTabsLayout()

        sharedPreferences =
            this.getSharedPreferences(Constants.ADVOGO_PREFERENCES, Context.MODE_PRIVATE)

        advRepository.obterAdvogado(
            getCurrentUserID(),
            { adv ->
                this@MainActivity.adv = adv
                setNavigationAdvDetalhes(adv)

                val tokenUpdated = sharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

                if (tokenUpdated) {
                    advRepository.obterAdvogado(
                        getCurrentUserID(),
                        { adv ->
                            this@MainActivity.adv = adv
                            setNavigationAdvDetalhes(adv)
                        },
                        { ex -> null } //TODO("Imlementar OnFailure")
                    )
                } else {
                    FirebaseMessaging.getInstance()
                        .token
                        .addOnSuccessListener(this@MainActivity) { instanceIdResult ->
                            updateFCMToken(adv, instanceIdResult)
                        }.addOnFailureListener {
                            Log.e("TOKEN", "Erro ao gerar novo token para o usuário")
                        }
                }
            },
            { ex -> null } //TODO("Imlementar OnFailure")
        )

        binding.navView.setNavigationItemSelectedListener(this)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data!!.hasExtra(Constants.FROM_PERFIL_ACTIVITY)) {
                    advRepository.obterAdvogado(
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
        adapter.addFragment(ProcessosFragment(), "Processos")
        adapter.addFragment(ClienteFragment(), "Clientes")
        adapter.addFragment(DiligenciasFragment(), "Diligências")
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customView = LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.item_tab_layout, tabLayout, false)

            val tabIcon: ImageView = customView.findViewById(R.id.tab_icon)
            val tabTitle: TextView = customView.findViewById(R.id.tab_title)

            tabIcon.setImageResource(getTabIcon(position))
            tabTitle.text = adapter.getTabTitle(position)

            tab.customView = customView
        }.attach()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.navPerfil -> {
                val intent = Intent(this@MainActivity, PerfilActivity::class.java)
                intent.putExtra(Constants.FROM_PERFIL_ACTIVITY, Constants.FROM_PERFIL_ACTIVITY)
                resultLauncher.launch(intent)
            }
            R.id.navAdvogados -> {
                val intent = Intent(this@MainActivity, AdvogadoActivity::class.java)
                startActivity(intent)
            }
            R.id.navProcessoStatus -> {
                val intent = Intent(this@MainActivity, ProcessoStatusActivity::class.java)
                startActivity(intent)
            }
            R.id.navProcessoTipos -> {
                val intent = Intent(this@MainActivity, ProcessoTiposActivity::class.java)
                startActivity(intent)
            }
            R.id.navDiligenciaStatus -> {
                val intent = Intent(this@MainActivity, DiligenciaStatusActivity::class.java)
                startActivity(intent)
            }
            R.id.navDiligenciaTipos -> {
                val intent = Intent(this@MainActivity, DiligenciaTiposActivity::class.java)
                startActivity(intent)
            }
            R.id.navAndamentoStatus -> {
                val intent = Intent(this@MainActivity, ProcessoAndamentoStatusActivity::class.java)
                startActivity(intent)
            }
            R.id.navAndamentoTipos -> {
                val intent = Intent(this@MainActivity, ProcessoAndamentoTiposActivity::class.java)
                startActivity(intent)
            }
//            R.id.navAnexoTipos -> {
//                val intent = Intent(this@MainActivity, AnexoTiposActivity::class.java)
//                startActivity(intent)
//            }
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

    @RequiresApi(Build.VERSION_CODES.P)
    private fun setupActionBar() {
        setSupportActionBar(binding.appBarMain.toolbarMain)
        binding.appBarMain.toolbarMain.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        binding.appBarMain.toolbarMain.setNavigationOnClickListener {
            toggleDrawer()
        }

        val actionBar = supportActionBar

        if(actionBar != null) {
            val spannableTitle = SpannableString(getString(R.string.app_name))

            spannableTitle.setSpan(
                TypefaceSpan(ResourcesCompat.getFont(this, R.font.montserrat_medium)!!),
                0,
                title.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )

            actionBar.title = spannableTitle
        }
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            0 -> R.drawable.ic_baseline_shopping_bag_24
            1 -> R.drawable.ic_baseline_contacts_24
            2 -> R.drawable.ic_baseline_event_24
            else -> 0
        }
    }
}