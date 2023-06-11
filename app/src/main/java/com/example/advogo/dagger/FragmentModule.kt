//package com.example.advogo.dagger
//
//import android.app.Application
//import android.app.ProgressDialog
//import android.content.Context
//import androidx.fragment.app.Fragment
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.FragmentComponent
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(FragmentComponent::class)
//object FragmentModule {
//    @Provides
//    fun provideContext(fragment: Fragment): Context {
//        return fragment.requireContext()
//    }
//
//    @Provides
//    fun provideProgressDialog(context: Context): ProgressDialog {
//        return ProgressDialog(context)
//    }
//}

////@Module
////@InstallIn(SingletonComponent::class)
////object ObjectModule {
////    @Provides
////    @Singleton
////    fun provideProgressDialog(application: Application): ProgressDialog {
////        return ProgressDialog(application)
////    }
////
////    @Provides
////    @Singleton
////    fun provideApplicationContext(application: Application): Context {
////        return application
////    }
////}
