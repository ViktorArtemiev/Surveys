package co.nimblehq.screen.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.nimblehq.R
import co.nimblehq.data.model.Survey
import co.nimblehq.extension.loadImage
import kotlinx.android.synthetic.main.item_survey.view.*


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */

class SurveysAdapter : PagedListAdapter<Survey, RecyclerView.ViewHolder>(SurveyDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView = inflateItemView(parent, R.layout.item_survey)
        return SurveyViewHolder(itemView)
    }

    private fun inflateItemView(parent: ViewGroup, @LayoutRes layoutRes: Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SurveyViewHolder).bindSurvey(getItem(position)!!)
    }
}

class SurveyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindSurvey(survey: Survey) {
        itemView.image_view.loadImage(survey.image)
        itemView.text_view_title.text = survey.title
        itemView.text_view_description.text = survey.description
    }

}

class SurveyDiffCallback : DiffUtil.ItemCallback<Survey>() {

    override fun areItemsTheSame(oldItem: Survey, newItem: Survey): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Survey, newItem: Survey): Boolean {
        return oldItem == newItem
    }

}