package com.mackenzie.di.component

import com.mackenzie.DLApplication
import com.mackenzie.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityBindingModule::class, UtilModule::class,
        DatabaseModule::class, NetworkModule::class, RepositoryModule::class, ViewModelModule::class, MyWorkerModule::class, ServiceBuilderModule::class]
)
interface AppComponent : AndroidInjector<DLApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: DLApplication): Builder

        fun build(): AppComponent
    }
}