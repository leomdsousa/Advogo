package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.advogo.repositories.IClienteRepository
import javax.inject.Inject

class ClientesService : Service() {
    @Inject lateinit var repository: IClienteRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}