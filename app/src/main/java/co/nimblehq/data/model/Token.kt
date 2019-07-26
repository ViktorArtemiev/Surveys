package co.nimblehq.data.model

import com.google.gson.annotations.SerializedName


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
data class Token(@SerializedName("access_token") val accessToken: String,
                 @SerializedName("token_type") val type: String,
                 @SerializedName("expires_in") val expiresIn: Int)