package com.example.advogo.dagger

import com.example.advogo.repositories.*
import com.google.firebase.firestore.FirebaseFirestore
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

    @Provides
    fun provideTelefoneRepository(): ITelefoneRepository {
        return TelefoneRepository()
    }

//    @Provides
//    fun provideProcessoRepository(firestore: FirebaseFirestore): IProcessoRepository {
//        return ProcessoRepository(firestore)
//    }
//
//    @Provides
//    fun provideAdvogadoRepository(firestore: FirebaseFirestore): IAdvogadoRepository {
//        return AdvogadoRepository(firestore)
//    }
//
//    @Provides
//    fun provideDiligenciaRepository(firestore: FirebaseFirestore): IDiligenciaRepository {
//        return DiligenciaRepository(firestore)
//    }
//
//    @Provides
//    fun provideTelefoneRepository(firestore: FirebaseFirestore): ITelefoneRepository {
//        return TelefoneRepository(firestore)
//    }
}