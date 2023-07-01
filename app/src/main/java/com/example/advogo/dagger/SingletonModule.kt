package com.example.advogo.dagger

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.advogo.services.CalendarService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {
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
    fun provideCalendarService(): CalendarService {
        return CalendarService()
    }
}
