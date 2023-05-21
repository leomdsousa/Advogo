package com.example.advogo.dagger

//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleOwner
//import androidx.lifecycle.LifecycleRegistry
//import androidx.lifecycle.lifecycleScope
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.components.ActivityComponent
//import dagger.hilt.android.components.FragmentComponent
//import kotlinx.coroutines.CoroutineScope
//
//@Module
//@InstallIn(FragmentComponent::class)
//object LifecycleScopeModule {
//    @Provides
//    //@Singleton
//    fun provideLifecycleOwner(): LifecycleOwner {
//        return object : LifecycleOwner {
//            private val lifecycleRegistry = LifecycleRegistry(this)
//
//            override val lifecycle: Lifecycle
//                get() = lifecycleRegistry
//        }
//    }
//
//    @Provides
//    //@Singleton
//    fun provideLifecycleScopeProvider(lifecycleOwner: LifecycleOwner): LifecycleScopeProvider {
//        return object : LifecycleScopeProvider {
//            override val lifecycleScope: CoroutineScope
//                get() = lifecycleOwner.lifecycleScope
//        }
//    }
//}
//
//interface LifecycleScopeProvider {
//    val lifecycleScope: CoroutineScope
//}
