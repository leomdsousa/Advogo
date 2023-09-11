package com.example.advogo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.advogo.R
import com.example.advogo.databinding.ActivityMapBinding
import com.example.advogo.models.Advogado
import com.example.advogo.models.Diligencia
import com.example.advogo.models.Processo
import com.example.advogo.utils.constants.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    private lateinit var localizacao: Localizacao

    private var processo: Processo? = null
    private var diligencia: Diligencia? = null
    private var advogado: Advogado? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if(intent.hasExtra(Constants.PROCESSO_MAP)) {
            processo = intent.getParcelableExtra(Constants.PROCESSO_MAP) as Processo?

            localizacao = Localizacao(
                title = "",
                location = "",
                latitude = 0.0,
                longitude = 0.0
            )
        } else if (intent.hasExtra(Constants.DILIGENCIA_MAP)) {
            diligencia = intent.getParcelableExtra(Constants.DILIGENCIA_MAP) as Diligencia?

            localizacao = Localizacao(
                title = diligencia!!.descricao!!,
                location = diligencia!!.endereco!!,
                latitude = diligencia!!.enderecoLat!!,
                longitude = diligencia!!.enderecoLong!!
            )
        } else if (intent.hasExtra(Constants.ADVOGADO_MAP)) {
            advogado = intent.getParcelableExtra(Constants.ADVOGADO_MAP) as Advogado?

            localizacao = Localizacao(
                title = "",
                location = "",
                latitude = 0.0,
                longitude = 0.0
            )
        }

        if(localizacao != null) {
            setSupportActionBar(binding.toolbarMap)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = localizacao!!.title

            binding.toolbarMap.setNavigationOnClickListener {
                onBackPressed()
            }

            val supportMapFragment: SupportMapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

            supportMapFragment.getMapAsync(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val position = LatLng(localizacao!!.latitude, localizacao!!.longitude)
        val titleLocation = localizacao!!.location

        googleMap!!.addMarker(MarkerOptions().position(position).title(titleLocation))
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(position, 15f)
        googleMap.animateCamera(newLatLngZoom)
    }

    inner class Localizacao(
        var title: String = "",
        var location: String = "",
        var latitude: Double = 0.0,
        var longitude: Double = 0.0
    ) { }
}