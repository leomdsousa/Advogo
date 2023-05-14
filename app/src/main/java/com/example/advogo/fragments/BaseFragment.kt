package com.example.advogo.fragments

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

open class BaseFragment : Fragment() {
    fun getCurrentUserID(): String {
        val user = FirebaseAuth.getInstance().currentUser

        return if(user != null) {
            FirebaseAuth.getInstance().currentUser!!.uid
        } else {
            ""
        }
    }
}