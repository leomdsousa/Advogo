package com.example.advogo.activities
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.advogo.R
import com.example.advogo.repositories.IAdvogadoRepository
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {
    private lateinit var _sharedPreferences: SharedPreferences
    private lateinit var _result: ActivityResultLauncher<Intent>
    private var _doubleBackToExitPressureOnce = false

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
        TODO("Mudar para um singleton")
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

    companion object {
        const val READ_STORAGE_PERMISSION_CODE = 1
        const val PICK_IMAGE_REQUEST_CODE = "PICK_IMAGE_REQUEST_CODE"
    }
}