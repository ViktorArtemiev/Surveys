package co.nimblehq

import co.nimblehq.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber


/**
 * Created by Viktor Artemiev on 2019-07-25.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveysApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<SurveysApp> {
        return DaggerAppComponent.factory().create(this@SurveysApp)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}