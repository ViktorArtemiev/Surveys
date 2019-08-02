package co.nimblehq.di.module

import co.nimblehq.SurveysApp
import co.nimblehq.account.Account
import co.nimblehq.account.AccountImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Module
class AccountModule {

    @Provides
    @Singleton
    fun provideAccount(app: SurveysApp): Account {
        return AccountImpl(app)
    }
}