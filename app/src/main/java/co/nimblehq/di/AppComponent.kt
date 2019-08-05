package co.nimblehq.di

import co.nimblehq.SurveysApp
import co.nimblehq.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ViewModelModule::class,
        MainActivityModule::class,
        DataModule::class,
        NetworkModule::class,
        AccountModule::class]
)
interface AppComponent : AndroidInjector<SurveysApp> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: SurveysApp): AppComponent
    }
}