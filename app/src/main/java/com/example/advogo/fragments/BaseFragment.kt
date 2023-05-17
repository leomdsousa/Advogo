package com.example.advogo.fragments

import android.app.ProgressDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

open class BaseFragment : Fragment() {
    //@Inject private lateinit var progressDialog: ProgressDialog

//    class BaseFragment @Inject constructor(
//        private val progressDialog: ProgressDialog
//    ) { }

//    fun showProgressDialog() {
//        progressDialog.show()
//    }
//
//    fun hideProgressDialog() {
//        progressDialog.show()
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