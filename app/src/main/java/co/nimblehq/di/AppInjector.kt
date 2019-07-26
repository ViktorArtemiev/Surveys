package co.nimblehq.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import co.nimblehq.SurveysApp
import dagger.android.AndroidInjection


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
object AppInjector {
    fun init(app: SurveysApp) {
        DaggerAppComponent.builder()
            .application(app)
            .build()
            .inject(app)

        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                handleActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })
    }

    fun handleActivity(activity: Activity) {
        if (activity is Injectable) {
            AndroidInjection.inject(activity)
        }
    }

}