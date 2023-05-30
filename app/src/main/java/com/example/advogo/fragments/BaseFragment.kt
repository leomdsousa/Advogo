package com.example.advogo.fragments

import android.app.ProgressDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

open class BaseFragment : Fragment() {
//    class BaseFragment @Inject constructor(
//        private val progressDialog: ProgressDialog
//    ) { }
//        @Inject lateinit var progressDialog: ProgressDialog

//    fun showProgressDialog() {
//        progressDialog.show()
//    }
//
//    fun hideProgressDialog() {
//        progressDialog.hide()
//    }

    fun getCurrentUserID(): String {
        val user = FirebaseAuth.getInstance().currentUser

        return if(user != null) {
            FirebaseAuth.getInstance().currentUser!!.uid
        } else {
            ""
        }
    }
}