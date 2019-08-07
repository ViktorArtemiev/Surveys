package co.nimblehq.rule

import android.app.Activity
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.rule.IntentsTestRule
import co.nimblehq.TestSurveysApp
import dagger.android.AndroidInjector


/**
 * Created by Viktor Artemiev on 2019-08-07.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class InjectableActivityIntentsTestRule<T : Activity>(activityClass: Class<T>,
                                                      initialTouchMode: Boolean = false,
                                                      launchActivity: Boolean = false,
                                                      val inject: (T) -> Unit)
    : IntentsTestRule<T>(activityClass, initialTouchMode, launchActivity) {

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        ApplicationProvider.getApplicationContext<TestSurveysApp>().run {
            injector = AndroidInjector {
                inject(it as T)
            }
        }
    }
}