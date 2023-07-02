package com.example.advogo.activities
import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TypefaceSpan
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.example.advogo.R
import com.example.advogo.models.Advogado
import com.example.advogo.repositories.AdvogadoRepository
import com.example.advogo.utils.Constants
import com.example.advogo.utils.ObterEnderecoFromLatLng
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    @Inject lateinit var progressDialog: ProgressDialog
    @Inject lateinit var baseAdvogadoRepository: AdvogadoRepository

    private lateinit var _sharedPreferences: SharedPreferences
    private lateinit var _result: ActivityResultLauncher<Intent>
    private var _doubleBackToExitPressureOnce = false

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    fun showProgressDialog(texto: String? = null) {
        if (!::progressDialog.isInitialized || !progressDialog.isShowing) {
            progressDialog = ProgressDialog(this)
            progressDialog.setCancelable(false)
            if (texto != null) {
                progressDialog.setTitle(texto)
            }
            progressDialog.show()
        }
    }

    fun hideProgressDialog() {
        if(progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun isUserLoggedIn(): Boolean {
        return getCurrentUserID().isNotEmpty()
    }

    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun getCurrentUserID(): String {
        val user = FirebaseAuth.getInstance().currentUser

        return if(user != null) {
            FirebaseAuth.getInstance().currentUser!!.uid
        } else {
            ""
        }
    }

    fun doubleBackToExit() {
        if(_doubleBackToExitPressureOnce) {
            super.onBackPressed()
            return
        }

        this._doubleBackToExitPressureOnce = true
        Toast.makeText(this, R.string.cliqueNovamente, Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({}, 2000)
    }

    fun showErrorSnackBar(msg: String) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        snackBar.show()
    }

    fun chooseImage(activity: Activity, result: ActivityResultLauncher<Intent>) {
        _result = result

        if(ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            showImageChooser(_result)
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun showImageChooser(result: ActivityResultLauncher<Intent>) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        galleryIntent.putExtra(PICK_IMAGE_REQUEST_CODE, PICK_IMAGE_REQUEST_CODE)
        result.launch(galleryIntent)
    }

    fun showDataPicker(onSuccess: (year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                onSuccess(year, monthOfYear, dayOfMonth)
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_STORAGE_PERMISSION_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser(_result)
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    fun abrirArquivo(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        if(consegueAbrirArquivo(intent)) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Nenhum aplicativo encontrado para visualizar o PDF.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun consegueAbrirArquivo(intent: Intent): Boolean {
        val packageManager = packageManager
        return intent.resolveActivity(packageManager) != null
    }

    fun showGoogleMapPlaces(context: Context, result: ActivityResultLauncher<Intent>) {
        //_result = result

        try {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )

            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fields
            ).build(context)
            intent.putExtra(Constants.FROM_GOOGLE_PLACES, Constants.FROM_GOOGLE_PLACES)

            val extraBundle = Bundle().apply {
                putBoolean(Constants.FROM_GOOGLE_PLACES, true)
            }

            val intentWithExtra = Intent(intent).apply {
                putExtras(extraBundle)
            }

            result.launch(intentWithExtra)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun obterLocalizacaoAtual(
        context: Context,
        onSuccess: (lat: Double, long: Double) -> Unit,
        onFailure: () -> Unit
    ) {
        if(!isLocationEnabled()) {
            Toast.makeText(this, "Localização não habilitada, favor habilite-a e tente novamente.", Toast.LENGTH_SHORT).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object: MultiplePermissionsListener {
                    @SuppressLint("MissingPermission")
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if(report!!.areAllPermissionsGranted()) {
                            fusedLocationProviderClient.lastLocation
                                .addOnSuccessListener { location : Location? ->
                                    if(location != null) {
                                        onSuccess(location.latitude, location.longitude)
                                    } else {
                                        onFailure()
                                    }
                                }
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions(context)
                    }
                })
                .onSameThread()
                .check()
        }
    }

    private fun obterLocalizacaoComLatLong(
        context: Context,
        lat: Double,
        lng: Double,
        onSuccess: (endereco: String) -> Unit,
        onFailure: () -> Unit
    ) {
        val addressTask = ObterEnderecoFromLatLng(context, lat, lng)
        addressTask.setCustomAddressListener(object: ObterEnderecoFromLatLng.AddressListener {
            override fun onAddressFound(address: String) {
                onSuccess(address)
            }
            override fun onError() {
                onFailure()
            }
        })

        lifecycleScope.launch(Dispatchers.IO) {
            addressTask.launchBackgroundProcessForRequest()
        }
    }

//    private val locationCallback = object: LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            val lastLocation: Location? = locationResult!!.lastLocation
//            savedLatitude = lastLocation!!.latitude
//            savedLongitude = lastLocation!!.longitude
//
//            val addressTask = ObterEnderecoFromLatLng(this@AddLugarFavoritoActivity, savedLatitude, savedLongitude)
//            addressTask.setCustomAddressListener(object: ObterEnderecoFromLatLng.AddressListener {
//                override fun onAddressFound(address: String) {
//                    binding.etLocation.setText(address)
//                }
//
//                override fun onError() {
//                    Log.e("Address:: ", "onError: Um erro ocorreu ao traduzir as coordenadas para o endereço")
//                }
//            })
//
//            lifecycleScope.launch(Dispatchers.IO) {
//                addressTask.launchBackgroundProcessForRequest()
//            }
//        }
//    }

    private fun showRationalDialogForPermissions(context: Context) {
        AlertDialog
            .Builder(context)
            .setMessage("Permissões negadas! Você ainda pode permiti-las em Configurações do Sistema.")
            .setPositiveButton("Ir para Configurações")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

//    @SuppressLint("MissingPermission")
//    private fun requestNewLocationData(){
//        var mLocationRequest = LocationRequest.create().apply {
//            interval = 5000
//            fastestInterval = 1000
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//
//        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback,
//            Looper.myLooper()!!
//        )
//    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun setupActionBar(text: String, toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)

            val spannableTitle = SpannableString(text)

            spannableTitle.setSpan(
                TypefaceSpan(ResourcesCompat.getFont(this, R.font.montserrat_medium)!!),
                0,
                text.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )

            actionBar.title = spannableTitle
        }
    }

    companion object {
        const val READ_STORAGE_PERMISSION_CODE = 1
        const val PICK_IMAGE_REQUEST_CODE = "PICK_IMAGE_REQUEST_CODE"
    }

    fun updateFCMToken(usuario: Advogado, token: String) {
        usuario.fcmToken = token
        baseAdvogadoRepository.atualizarAdvogado(
            usuario,
            { tokenUpdateSuccess() },
            { null }
        )
    }

    private fun tokenUpdateSuccess() {
        _sharedPreferences =
            this.getSharedPreferences(Constants.ADVOGO_PREFERENCES, Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = _sharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
    }

    fun showFileChooser(result: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"

        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
            "image/jpeg",
            "image/png",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ))
        intent.putExtra(Constants.FROM_FILE_CHOOSE, Constants.FROM_FILE_CHOOSE)

        result.launch(Intent.createChooser(intent, "Escolha um arquivo"))
    }

    fun openGoogleMaps(endereco: String) {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(endereco)}")

        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    fun openGoogleMaps(latitude: Double, longitude: Double) {
        val uri = Uri.parse("geo:$latitude,$longitude")

        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    }
}