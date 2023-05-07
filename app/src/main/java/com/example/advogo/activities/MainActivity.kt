package com.example.advogo.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import com.example.advogo.R
import com.example.advogo.databinding.ActivityMainBinding
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
                //if (result.data!!.hasExtra(PROFILE_REQUEST_CODE)) {
                    //FirestoreService().loadUserData(this@MainActivity)
                //} else if (result.data!!.hasExtra(CREATE_BOARD_REQUEST_CODE)) {
                    //FirestoreService().getBoardsList(this@MainActivity)
                //}
            } else {
                Log.e("Cancelado", "Cancelado")
            }
        }

        binding.appBarMain.fabCreateBoard.setOnClickListener {
            val intent = Intent(this@MainActivity, ProcessoCadastroActivity::class.java)
            intent.putExtra(Constants.ADV_NOME_PARAM, advNome)
            resultLauncher.launch(intent)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when(menuItem.itemId) {
            R.id.navPerfil -> {
                val intent = Intent(this@MainActivity, PerfilActivity::class.java)
                //intent.putExtra(PROFILE_REQUEST_CODE, PROFILE_REQUEST_CODE)
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