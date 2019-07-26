package co.nimblehq.screen.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.nimblehq.R
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Viktor Artemiev on 2019-07-25.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.inflateMenu(R.menu.menu_main)
    }
}
