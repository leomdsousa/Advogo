package com.example.advogo.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.advogo.R
import com.example.advogo.databinding.ActivityDiligenciaCadastroBinding
import com.example.advogo.models.Diligencia
import com.example.advogo.repositories.DiligenciaRepository
import com.example.advogo.utils.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import java.io.IOException
import javax.inject.Inject

class DiligenciaCadastroActivity : BaseActivity() {
    private lateinit var binding: ActivityDiligenciaCadastroBinding
    @Inject lateinit var diligenciaRepository: DiligenciaRepository

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    private var savedUriImage: Uri? = null
    private var savedLatitude: Double = 0.0
    private var savedLongitude: Double = 0.0

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDiligenciaCadastroBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        setupActionBar()
        configurarGoogleMapPlaces()

        binding.btnCadastrarDiligencia.setOnClickListener {
            saveDiligencia()
        }

        binding.etDiligenciaEndereco.setOnClickListener {
            showGoogleMapPlaces(this, resultLauncher)
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
//                    if(result.data!!.hasExtra(Constants.FROM_DEVICE_GALLERY)) {
//                        val contentUri = result.data!!.data
//                        var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
//                        savedUriImage = saveImageToInternalStorage(bitmap)
//                        Log.e("Info", savedUriImage?.path.toString())
//                        binding.ivPlace.setImageURI(contentUri)
//                    } else if(result.data!!.hasExtra(Constants.FROM_DEVICE_CAMERA)) {
//                        val contentBitmap: Bitmap = data.extras!!.get("data") as Bitmap
//                        savedUriImage = saveImageToInternalStorage(contentBitmap)
//                        Log.e("Info", savedUriImage?.path.toString())
//                        binding.ivPlace.setImageBitmap(contentBitmap)
//                    }
                    if(result.data!!.hasExtra(Constants.FROM_GOOGLE_PLACES)) {
                        val place: Place = Autocomplete.getPlaceFromIntent(result.data!!)
                        binding.etDiligenciaEndereco.setText(place.address)
                        savedLatitude = place.latLng!!.latitude
                        savedLongitude = place.latLng!!.longitude
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarDiligenciaCadastro)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = "Cadastro Diligência"
        }

        binding.toolbarDiligenciaCadastro.setNavigationOnClickListener { onBackPressed() }
    }

    private fun saveDiligencia() {
        //TODO("showProgressDialog("Please wait...")")

        //TODO("preencher obj para add ou alterar")
        val diligencia = Diligencia(

        )

        diligenciaRepository.AdicionarDiligencia(
            diligencia,
            { diligenciaCadastroSuccess() },
            { diligenciaCadastroFailure() }
        )
    }

    private fun diligenciaCadastroSuccess() {
        //TODO("hideProgressDialog()")
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun diligenciaCadastroFailure() {
        //TODO("hideProgressDialog()")

        Toast.makeText(
            this@DiligenciaCadastroActivity,
            "Um erro ocorreu ao criar a diligência.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun configurarGoogleMapPlaces() {
        if(!Places.isInitialized()) {
            Places.initialize(this@DiligenciaCadastroActivity,
                resources.getString(R.string.google_maps_api_key))
        }
    }
}