package co.nimblehq.util

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAssertion
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`


/**
 * Created by Viktor Artemiev on 2019-08-07.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

fun checkRecyclerViewItemCount(count: Int) = ViewAssertion { view, noViewFoundException ->
    if (noViewFoundException != null) {
        throw noViewFoundException
    }

    if (view is RecyclerView) {
        assertThat(view.adapter?.itemCount, `is`(count))
    } else {
        throw noViewFoundException
    }
}