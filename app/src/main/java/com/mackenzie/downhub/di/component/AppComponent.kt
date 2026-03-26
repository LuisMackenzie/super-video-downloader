package com.mackenzie.downhub.di.component

import com.mackenzie.downhub.DLApplication
import com.mackenzie.downhub.di.module.ActivityBindingModule
import com.mackenzie.downhub.di.module.AppModule
import com.mackenzie.downhub.di.module.DatabaseModule
import com.mackenzie.downhub.di.module.MyWorkerModule
import com.mackenzie.downhub.di.module.NetworkModule
import com.mackenzie.downhub.di.module.RepositoryModule
import com.mackenzie.downhub.di.module.ServiceBuilderModule
import com.mackenzie.downhub.di.module.UtilModule
import com.mackenzie.downhub.di.module.ViewModelModule
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