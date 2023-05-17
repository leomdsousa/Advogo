package com.example.advogo.dagger

import android.app.Application
import android.app.ProgressDialog
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ObjectModule {
    @Provides
    @Singleton
    fun provideProgressDialog(application: Application): ProgressDialog {
        return ProgressDialog(application)
    }
}
