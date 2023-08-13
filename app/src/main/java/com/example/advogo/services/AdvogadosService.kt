package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.advogo.repositories.IAdvogadoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdvogadosService : Service() {
    @Inject lateinit var repository: IAdvogadoRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}