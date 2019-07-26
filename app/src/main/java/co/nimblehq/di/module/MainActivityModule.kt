package co.nimblehq.di.module

import co.nimblehq.screen.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}