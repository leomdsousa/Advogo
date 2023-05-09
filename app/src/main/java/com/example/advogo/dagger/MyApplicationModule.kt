package com.example.advogo.dagger

import com.example.advogo.repositories.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object MyApplicationModule {
    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideAdvogadoRepository(
        firestore: FirebaseFirestore
    ): IAdvogadoRepository {
        return AdvogadoRepository(firestore)
    }

    @Provides
    fun provideClienteRepository(
        firestore: FirebaseFirestore
    ): IClienteRepository {
        return ClienteRepository(firestore)
    }

    @Provides
    fun provideDiligenciaRepository(
        firestore: FirebaseFirestore,
        processoRepository: Provider<ProcessoRepository>,
        advogadoRepository: Provider<AdvogadoRepository>
    ): IDiligenciaRepository {
        return DiligenciaRepository(firestore, processoRepository, advogadoRepository)
    }

    @Provides
    fun provideEnderecoRepository(firestore: FirebaseFirestore): IEnderecoRepository {
        return EnderecoRepository(firestore)
    }

    @Provides
    fun provideProcessoRepository(
        firestore: FirebaseFirestore,
        advogadoRepository: AdvogadoRepository,
        clienteRepository: ClienteRepository,
        diligenciaRepository: Provider<DiligenciaRepository>,
        tipoProcessoRepository: ProcessoTipoRepository,
        statusProcessoRepository: ProcessoStatusRepository
    ): IProcessoRepository {
        return ProcessoRepository(
            firestore,
            advogadoRepository,
            clienteRepository,
            diligenciaRepository,
            tipoProcessoRepository,
            statusProcessoRepository
        )
    }

    @Provides
    fun provideProcessoStatusRepository(firestore: FirebaseFirestore): IProcessoStatusRepository {
        return ProcessoStatusRepository(firestore)
    }

    @Provides
    fun provideProcessoTipoRepository(firestore: FirebaseFirestore): IProcessoTipoRepository {
        return ProcessoTipoRepository(firestore)
    }

    @Provides
    fun provideTelefoneRepository(
        firestore: FirebaseFirestore,
        telefoneTipoRepository: TelefoneTipoRepository
    ): ITelefoneRepository {
        return TelefoneRepository(firestore, telefoneTipoRepository)
    }

    @Provides
    fun provideTelefoneTipoRepository(firestore: FirebaseFirestore): ITelefoneTipoRepository {
        return TelefoneTipoRepository(firestore)
    }
}