package co.nimblehq.runner

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import co.nimblehq.TestSurveysApp


/**
 * Created by Viktor Artemiev on 2019-08-07.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveysAndroidJUnitRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestSurveysApp::class.java.name, context)
    }
}