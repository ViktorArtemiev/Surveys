package co.nimblehq.util

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher


/**
 * Created by Viktor Artemiev on 2019-08-07.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

fun waitFor(delay: Long) = object : ViewAction {
    override fun getConstraints(): Matcher<View> {
        return ViewMatchers.isRoot()
    }

    override fun getDescription(): String {
        return "wait for " + delay + "milliseconds"
    }

    override fun perform(uiController: UiController, view: View) {
        uiController.loopMainThreadForAtLeast(delay)
    }
}