package co.nimblehq.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.nimblehq.screen.ViewModelFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import kotlin.reflect.KClass


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)