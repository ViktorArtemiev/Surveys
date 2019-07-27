package co.nimblehq.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Parcelize
data class Survey(val id: String,
                  val title: String,
                  val description: String,
                  @SerializedName("cover_image_url") val image: String) : Parcelable