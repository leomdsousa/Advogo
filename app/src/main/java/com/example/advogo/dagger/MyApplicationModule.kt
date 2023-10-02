package com.example.advogo.dagger

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.widget.ProgressBar
import com.example.advogo.repositories.*
import com.example.advogo.utils.handlers.CalendarHandler
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
    fun provideApplicationContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideProgressDialog(application: Application): ProgressDialog {
        return ProgressDialog(application)
    }

    @Provides
    @Singleton
    fun provideProgressBar(context: Context): ProgressBar {
        return ProgressBar(context)
    }

    @Provides
    @Singleton
    fun provideCalendarService(): CalendarHandler {
        return CalendarHandler()
    }

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
        statusDiligenciaRepository: DiligenciaStatusRepository,
        diligenciaHistoricoRepository: DiligenciaHistoricoRepository
    ): IDiligenciaRepository {
        return DiligenciaRepository(
            context,
            firestore,
            processoRepository,
            advogadoRepository,
            tipoDiligenciaRepository,
            statusDiligenciaRepository,
            diligenciaHistoricoRepository
        )
    }

    @Provides
    @Singleton
    fun provideDiligenciaHistoricoRepository(
        context: Context,
        firestore: FirebaseFirestore,
        advogadoRepository: AdvogadoRepository,
        tipoDiligenciaRepository: DiligenciaTipoRepository,
        statusDiligenciaRepository: DiligenciaStatusRepository
    ): IDiligenciaHistoricoRepository {
        return DiligenciaHistoricoRepository(
            context,
            firestore,
            advogadoRepository,
            tipoDiligenciaRepository,
            statusDiligenciaRepository
        )
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
        andamentoRepository: IProcessoAndamentoRepository,
        historicoRepository: IProcessoHistoricoRepository,
        tiposPartesRepository: ITipoParteRepository
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
            andamentoRepository,
            historicoRepository,
            tiposPartesRepository
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
    fun provideProcessoHistoricoRepository(
        context: Context,
        firestore: FirebaseFirestore,
        advogadoRepository: AdvogadoRepository,
        tipoProcessoRepository: ProcessoTipoRepository,
        statusProcessoRepository: ProcessoStatusRepository
    ): IProcessoHistoricoRepository {
        return ProcessoHistoricoRepository(
            context,
            firestore,
            advogadoRepository,
            tipoProcessoRepository,
            statusProcessoRepository
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
    fun provideAnexoRepository(
        context: Context,
        firestore: FirebaseFirestore,
        advogadoRepository: AdvogadoRepository
    ): IAnexoRepository {
        return AnexoRepository(context, firestore, advogadoRepository)
    }

    @Provides
    @Singleton
    fun provideTiposPartesRepository(
        firestore: FirebaseFirestore,
    ): ITipoParteRepository {
        return TipoParteRepository(firestore)
    }
}
