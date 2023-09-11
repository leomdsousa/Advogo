package com.example.advogo.utils.handlers

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.Window
import com.example.advogo.R


class ProgressDialogHandler {
    private var dialog: Dialog? = null
    private val ourInstance: ProgressDialogHandler = ProgressDialog()

    fun getInstance(): ProgressDialogHandler {
        return ourInstance
    }

    @SuppressLint("NotConstructor")
    fun ProgressDialog(): ProgressDialogHandler {
        return ProgressDialogHandler()
    }

    fun show(context: Context) {
        if (dialog != null) {
            if(dialog!!.isShowing) {
                return
            } else {
                dialog = Dialog(context)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.setContentView(R.layout.dialog_progress)
                dialog!!.setCancelable(true)
                dialog!!.show()
            }
        }
    }

    fun dismiss() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }
}