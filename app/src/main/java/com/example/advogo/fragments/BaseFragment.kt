package com.example.advogo.fragments

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.advogo.activities.BaseActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment() {
    private lateinit var _sharedPreferences: SharedPreferences
    private lateinit var _result: ActivityResultLauncher<Intent>
    private var _doubleBackToExitPressureOnce = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject lateinit var progressDialog: ProgressDialog
    @Inject lateinit var progressBar: ProgressBar

    fun showProgressDialog(texto: String? = null) {
        val activity = requireActivity()
        if (!activity.isFinishing && isAdded && !progressDialog.isShowing) {
            progressDialog = ProgressDialog(activity)
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

    private fun showProgressBar() {
        //progressBar.visibility = View.VISIBLE
        val progressBar = ProgressBar(requireContext())
        (view as? ViewGroup)?.addView(progressBar)
    }

    private fun hideProgressBar() {
        //progressBar.visibility = View.GONE
        (view as? ViewGroup)?.removeView(progressBar)
    }

    fun showDataPicker(context: Context, onSuccess: (year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            context,
            { _, year, monthOfYear, dayOfMonth ->
                onSuccess(year, monthOfYear, dayOfMonth)
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    fun chooseImage(fragment: Fragment, result: ActivityResultLauncher<Intent>) {
        _result = result

        if (ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showImageChooser(_result)
        } else {
            fragment.requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                BaseActivity.READ_STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun showImageChooser(result: ActivityResultLauncher<Intent>) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        galleryIntent.putExtra(
            BaseActivity.PICK_IMAGE_REQUEST_CODE,
            BaseActivity.PICK_IMAGE_REQUEST_CODE
        )
        result.launch(galleryIntent)
    }

    fun getFileExtension(uri: Uri): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(requireContext().contentResolver.getType(uri!!))
    }

    fun abrirArquivo(url: String) {
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.setDataAndType(Uri.parse(url), "application/pdf")
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//        if(consegueAbrirArquivo(intent)) {
//            startActivity(intent)
//        } else {
//            Toast.makeText(requireContext(), "Nenhum aplicativo encontrado para visualizar o PDF.", Toast.LENGTH_SHORT).show()
//        }

        val storageReference = Firebase.storage.getReferenceFromUrl(url)

        val localFile = File.createTempFile("temp", "pdf")

        storageReference.getFile(localFile)
            .addOnSuccessListener {
                val fileUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().packageName + ".fileprovider",
                    localFile
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(fileUri, "application/pdf")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                startActivity(intent)
//                if (consegueAbrirArquivo(intent)) {
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(
//                        requireContext(),
//                        "Nenhum aplicativo encontrado para visualizar o PDF.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao baixar o arquivo.", Toast.LENGTH_SHORT).show()
            }
    }

    fun deletarArquivo(caminhoArquivo: String, onSuccess: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.document(caminhoArquivo)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao excluir o documento", Toast.LENGTH_LONG).show()
            }
    }

    private fun consegueAbrirArquivo(intent: Intent): Boolean {
        val packageManager = requireContext().packageManager
        return intent.resolveActivity(packageManager) != null
    }

    fun getCurrentUserID(): String {
        val user = FirebaseAuth.getInstance().currentUser

        return if(user != null) {
            FirebaseAuth.getInstance().currentUser!!.uid
        } else {
            ""
        }
    }
}