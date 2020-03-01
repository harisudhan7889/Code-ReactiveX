package com.hari.subjects.common

import android.support.v7.widget.RecyclerView
import android.view.View
import com.hari.api.model.RestaurantObject
import kotlinx.android.synthetic.main.view_load_more_items.view.*

/**
 * @author Hari Hara Sudhan.N
 */
class MoreItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setData(restaurantObject: RestaurantObject) {
        itemView.moreItemsCount.text = "+" + 3
    }
}