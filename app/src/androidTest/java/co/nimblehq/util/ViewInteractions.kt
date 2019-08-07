package co.nimblehq.util

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.viewpager2.widget.ViewPager2
import org.hamcrest.CoreMatchers.allOf

/**
 * Created by Viktor Artemiev on 2019-08-07.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

/**
 * Creates a [ViewInteraction] that interacts with a [ViewPager2].
 */
fun onViewPager(): ViewInteraction {
    return onView(isAssignableFrom(ViewPager2::class.java))
}

/**
 * Creates a [ViewInteraction] that interacts with the currently visible page of a [ViewPager2]. The
 * currently visible page is the page that is displaying at least 50% of its content. When two pages
 * both show exactly 50%, the selected page is undefined.
 */
fun onCurrentPage(): ViewInteraction {
    return onView(allOf(
        withParent(withParent(isAssignableFrom(ViewPager2::class.java))),
        isDisplayingAtLeast(50)
    ))
}
