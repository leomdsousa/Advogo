package com.example.advogo.activities

import android.os.Bundle
import com.example.advogo.R
import com.example.advogo.repositories.IAdvogadoRepository
import com.example.advogo.repositories.IDiligenciaRepository
import com.example.advogo.repositories.IProcessoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject lateinit var _processoRepository: IProcessoRepository
    @Inject lateinit var _advRepository: IAdvogadoRepository
    @Inject lateinit var _diligenciaRepository: IDiligenciaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}