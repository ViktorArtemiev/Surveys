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
import kotlinx.android.synthetic.main.item_error.view.*
import kotlinx.android.synthetic.main.item_survey.view.*


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class SurveysAdapter(private val retryCallback: () -> Unit) :
    PagedListAdapter<Survey, RecyclerView.ViewHolder>(SurveyDiffCallback()) {

    enum class State {
        LOADING, ERROR, DONE
    }

    private var state: State = State.LOADING

    override fun onCreateViewHolder(parent: ViewGroup, @LayoutRes viewType: Int): RecyclerView.ViewHolder {
        val itemView = inflateItemView(parent, viewType)
        return when (viewType) {
            R.layout.item_loading -> LoadingViewHolder(itemView)
            R.layout.item_error -> ErrorViewHolder(retryCallback, itemView)
            else -> SurveyViewHolder(itemView)
        }
    }

    private fun inflateItemView(parent: ViewGroup, @LayoutRes layoutRes: Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SurveyViewHolder) holder.bindSurvey(super.getItem(position)!!)
    }

    @LayoutRes
    override fun getItemViewType(position: Int): Int {
        return if (hasExtraItem() && position == itemCount - 1) {
            when (state) {
                State.ERROR -> R.layout.item_error
                else -> R.layout.item_loading
            }
        } else {
            R.layout.item_survey
        }
    }

    fun setState(newState: State) {
        if (newState == this.state) return
        val hadExtraItem = hasExtraItem()
        this.state = newState
        val hasExtraItem = hasExtraItem()
        if (hadExtraItem != hasExtraItem) {
            if (hadExtraItem) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraItem) {
            notifyItemChanged(itemCount - 1)
        }
    }

    override fun getItemCount() = super.getItemCount() + if (hasExtraItem()) 1 else 0

    public override fun getItem(position: Int): Survey? {
        return if (hasExtraItem()) null
        else super.getItem(position)
    }

    private fun hasExtraItem() = state == State.LOADING || state == State.ERROR


}

class SurveyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindSurvey(survey: Survey) {
        itemView.image_view.loadImage(survey.image)
        itemView.text_view_title.text = survey.title
        itemView.text_view_description.text = survey.description
    }
}

class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class ErrorViewHolder(private val retryCallback: () -> Unit, itemView: View) :
    RecyclerView.ViewHolder(itemView) {

    init {
        itemView.button_retry.setOnClickListener { retryCallback() }
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