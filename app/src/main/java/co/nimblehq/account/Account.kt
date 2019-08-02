package co.nimblehq.account

import co.nimblehq.data.model.Token
import co.nimblehq.data.model.User


/**
 * Created by Viktor Artemiev on 2019-08-02.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
interface Account {

    fun provideUser(): User

    fun refreshToken(token: Token)

    fun provideToken(): String
}