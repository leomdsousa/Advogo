package com.example.advogo.dagger

import com.example.advogo.repositories.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object RepositoriosModule {

    @Provides
    fun provideProcessoRepository(): IProcessoRepository {
        return ProcessoRepository()
    }

    @Provides
    fun provideAdvogadoRepository(): IAdvogadoRepository {
        return AdvogadoRepository()
    }

    @Provides
    fun provideDiligenciaRepository(): IDiligenciaRepository {
        return DiligenciaRepository()
    }

}