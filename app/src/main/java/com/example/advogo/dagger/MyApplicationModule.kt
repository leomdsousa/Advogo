package com.example.advogo.dagger

import android.content.Context
import com.example.advogo.repositories.*
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MyApplicationModule {
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAdvogadoRepository(
        firestore: FirebaseFirestore
    ): IAdvogadoRepository {
        return AdvogadoRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideClienteRepository(
        firestore: FirebaseFirestore
    ): IClienteRepository {
        return ClienteRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideDiligenciaRepository(
        context: Context,
        firestore: FirebaseFirestore,
        processoRepository: Provider<ProcessoRepository>,
        advogadoRepository: Provider<AdvogadoRepository>,
        tipoDiligenciaRepository: DiligenciaTipoRepository,
        statusDiligenciaRepository: DiligenciaStatusRepository
    ): IDiligenciaRepository {
        return DiligenciaRepository(
            context,
            firestore,
            processoRepository,
            advogadoRepository,
            tipoDiligenciaRepository,
            statusDiligenciaRepository
        )
    }

    @Provides
    @Singleton
    fun provideDiligenciaHistoricoRepository(
        firestore: FirebaseFirestore
    ): IDiligenciaHistoricoRepository {
        return DiligenciaHistoricoRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideEnderecoRepository(firestore: FirebaseFirestore): IEnderecoRepository {
        return EnderecoRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideProcessoRepository(
        context: Context,
        firestore: FirebaseFirestore,
        advogadoRepository: AdvogadoRepository,
        clienteRepository: ClienteRepository,
        diligenciaRepository: Provider<DiligenciaRepository>,
        tipoProcessoRepository: ProcessoTipoRepository,
        statusProcessoRepository: ProcessoStatusRepository,
        anexoRepository: AnexoRepository,
        andamentoRepository: IProcessoAndamentoRepository
    ): IProcessoRepository {
        return ProcessoRepository(
            context,
            firestore,
            advogadoRepository,
            clienteRepository,
            diligenciaRepository,
            tipoProcessoRepository,
            statusProcessoRepository,
            anexoRepository,
            andamentoRepository
        )
    }

    @Provides
    @Singleton
    fun provideProcessoStatusRepository(firestore: FirebaseFirestore): IProcessoStatusRepository {
        return ProcessoStatusRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideProcessoTipoRepository(firestore: FirebaseFirestore): IProcessoTipoRepository {
        return ProcessoTipoRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideProcessoAndamentoRepository(
        context: Context,
        firestore: FirebaseFirestore,
        advogadoRepository: AdvogadoRepository,
        processoTipoAndamentoRepository: ProcessoTipoAndamentoRepository,
        processoStatusAndamentoRepository: ProcessoStatusAndamentoRepository
    ): IProcessoAndamentoRepository {
        return ProcessoAndamentoRepository(
            context,
            firestore,
            advogadoRepository,
            processoTipoAndamentoRepository,
            processoStatusAndamentoRepository
        )
    }

    @Provides
    @Singleton
    fun provideProcessoTipoAndamentoRepository(firestore: FirebaseFirestore): IProcessoTipoAndamentoRepository {
        return ProcessoTipoAndamentoRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideProcessoStatusAndamentoRepository(firestore: FirebaseFirestore): IProcessoStatusAndamentoRepository {
        return ProcessoStatusAndamentoRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideTelefoneRepository(
        firestore: FirebaseFirestore,
        telefoneTipoRepository: TelefoneTipoRepository
    ): ITelefoneRepository {
        return TelefoneRepository(firestore, telefoneTipoRepository)
    }

    @Provides
    @Singleton
    fun provideTelefoneTipoRepository(firestore: FirebaseFirestore): ITelefoneTipoRepository {
        return TelefoneTipoRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideDiligenciaTipoRepository(firestore: FirebaseFirestore): IDiligenciaTipoRepository {
        return DiligenciaTipoRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideDiligenciaStatusRepository(firestore: FirebaseFirestore): IDiligenciaStatusRepository {
        return DiligenciaStatusRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideAnexoRepository(firestore: FirebaseFirestore): IAnexoRepository {
        return AnexoRepository(firestore)
    }
}
