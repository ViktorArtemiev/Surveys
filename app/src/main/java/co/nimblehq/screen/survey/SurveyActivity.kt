package co.nimblehq.screen.survey

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.nimblehq.R
import co.nimblehq.data.model.Survey
import co.nimblehq.extension.loadImage
import kotlinx.android.synthetic.main.activity_survey.*


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveyActivity : AppCompatActivity() {

    companion object {

        const val EXTRA_SURVEY = "EXTRA_SURVEY"

        fun startIntent(context: Context, survey: Survey) =
            Intent(context, SurveyActivity::class.java)
            .apply { putExtra(EXTRA_SURVEY, survey) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)
        val survey = intent.getParcelableExtra<Survey>(EXTRA_SURVEY)
        toolbar.title = survey.title
        toolbar.setNavigationOnClickListener { onBackPressed() }
        image_view.loadImage(survey.getLargeImageUrl())
    }
}