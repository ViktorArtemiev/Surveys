package co.nimblehq

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.HasAndroidInjector


/**
 * Created by Viktor Artemiev on 2019-08-07.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class TestSurveysApp : Application(), HasAndroidInjector {

    var injector = AndroidInjector<Any> {
        // ignored
    }

    override fun androidInjector() = injector
}