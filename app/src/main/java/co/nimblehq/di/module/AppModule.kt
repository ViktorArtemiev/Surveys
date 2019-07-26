package co.nimblehq.di.module

import co.nimblehq.SurveysAccount
import co.nimblehq.SurveysApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Module
class AppModule {

    @Provides
    @Singleton
    fun provideAccount(app: SurveysApp): SurveysAccount {
        return SurveysAccount(app)
    }
}