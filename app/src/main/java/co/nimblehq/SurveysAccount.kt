package co.nimblehq

import android.content.Context
import co.nimblehq.data.model.Token
import co.nimblehq.data.model.User
import javax.inject.Singleton


/**
 * Created by Viktor Artemiev on 2019-07-26.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
@Singleton
class SurveysAccount(context: Context) {

    companion object {
        private const val ACCOUNT_PREF = "ACCOUNT_PREF"
        private const val KEY_TOKEN_TYPE = "KEY_TOKEN_TYPE"
        private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
    }

    private val preferences = context.getSharedPreferences(ACCOUNT_PREF, Context.MODE_PRIVATE)

    fun provideUser() = User()

    fun refreshToken(token: Token) {
        preferences.edit()
            .putString(KEY_TOKEN_TYPE, token.type)
            .putString(KEY_ACCESS_TOKEN, token.accessToken)
            .apply()
    }

    fun provideToken() = "${preferences.getString(KEY_TOKEN_TYPE, "bearer")} " +
            preferences.getString(KEY_ACCESS_TOKEN, "")

}