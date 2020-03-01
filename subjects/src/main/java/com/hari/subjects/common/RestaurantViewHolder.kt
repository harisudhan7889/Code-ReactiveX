package com.hari.subjects.common

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.hari.api.model.Restaurant
import com.hari.api.model.RestaurantObject
import kotlinx.android.synthetic.main.view_restaurant_grid.view.*

/**
 * @author Hari Hara Sudhan.N
 */
class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setData(restaurantObject: RestaurantObject) {
        val restaurant: Restaurant? = restaurantObject.restaurant
        itemView.restaurantName.text = restaurant?.name
        Glide.with(itemView.context)
            .load(restaurant?.thumbnailUrl)
            .into(itemView.restaurantImage)
    }

}