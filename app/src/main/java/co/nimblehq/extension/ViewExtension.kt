package co.nimblehq.extension

import android.widget.ImageView
import co.nimblehq.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */


/**
 * Load imageUrl into this {@link android.widget.ImageView}
 *
 * @param url
 */
fun ImageView.loadImage(url: String) {
    Glide.with(context)
        .load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .placeholder(R.color.blue_whale_alpha_50)
        .error(R.color.blue_whale_alpha_50)
        .centerCrop()
        .into(this)
}