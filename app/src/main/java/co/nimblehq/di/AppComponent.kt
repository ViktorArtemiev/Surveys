package co.nimblehq.di

import co.nimblehq.SurveysApp
import co.nimblehq.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
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
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(app: SurveysApp): Builder

        fun build(): AppComponent
    }

    fun inject(app: SurveysApp)
}