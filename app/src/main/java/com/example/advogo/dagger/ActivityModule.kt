//package com.example.advogo.dagger
//
//import android.app.Application
//import android.app.ProgressDialog
//import android.content.Context
//import androidx.fragment.app.Fragment
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ActivityComponent
//import dagger.hilt.android.components.FragmentComponent
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(ActivityComponent::class)
//object ActivityModule {
//    @Provides
//    fun provideProgressDialog(application: Application): ProgressDialog {
//        return ProgressDialog(application)
//    }
//}
