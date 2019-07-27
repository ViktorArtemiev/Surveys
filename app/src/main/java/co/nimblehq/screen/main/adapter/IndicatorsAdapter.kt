package co.nimblehq.screen.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.nimblehq.R


/**
 * Created by Viktor Artemiev on 2019-07-27.
 * Copyright (c) 2019, Nimble. All rights reserved.
 */
class IndicatorsAdapter : RecyclerView.Adapter<PageIndicatorViewHolder>() {

    private val indicators = mutableListOf<Indicator>()

    lateinit var recyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        this.recyclerView.layoutManager = object : LinearLayoutManager(recyclerView.context) {
            override fun canScrollVertically() = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageIndicatorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_indicator, parent, false)
        return PageIndicatorViewHolder(itemView)
    }

    override fun getItemCount() = indicators.size

    override fun onBindViewHolder(holder: PageIndicatorViewHolder, position: Int) {
        holder.bindIndicator(indicators[position])
    }

    fun setItemCount(count: Int) {
        val positionStart = indicators.size
        for (i in 0 until count - positionStart) {
            indicators.add(Indicator())
        }
        notifyItemRangeInserted(positionStart, count)
    }

    fun selectItemByPosition(position: Int) {
        if (position < 0 || position >= itemCount) return

        indicators[position].selected = true
        notifyItemChanged(position)
        recyclerView.scrollToPosition(position)

        val beforePosition = position - 1
        if (beforePosition >= 0 && indicators[beforePosition].selected) {
            indicators[beforePosition].selected = false
            notifyItemChanged(beforePosition)
        }

        val afterPosition = position + 1
        if (afterPosition < itemCount && indicators[afterPosition].selected) {
            indicators[afterPosition].selected = false
            notifyItemChanged(afterPosition)
        }
    }

    fun clear() {
        indicators.clear()
        notifyDataSetChanged()
    }

}

class PageIndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val indicatorImageView = itemView as ImageView

    fun bindIndicator(indicator: Indicator) {
        indicatorImageView.isEnabled = indicator.selected
    }

}

data class Indicator(var selected: Boolean = false)
