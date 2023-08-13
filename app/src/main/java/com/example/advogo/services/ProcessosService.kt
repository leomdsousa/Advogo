package com.example.advogo.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.advogo.repositories.IProcessoRepository
import javax.inject.Inject

class ProcessosService : Service() {
    @Inject lateinit var repository: IProcessoRepository

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}