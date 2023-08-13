package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.advogo.repositories.IDiligenciaRepository
import javax.inject.Inject

class DiligenciasService : Service() {
    @Inject lateinit var repository: IDiligenciaRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}