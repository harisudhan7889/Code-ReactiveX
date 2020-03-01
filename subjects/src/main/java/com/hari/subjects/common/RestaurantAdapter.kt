package com.hari.subjects.common

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hari.api.model.RestaurantObject
import com.hari.api.model.Type
import com.hari.subjects.R

/**
 * @author Hari Hara Sudhan.N
 */
class RestaurantAdapter(private val context: Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    private var restaurants: List<RestaurantObject>? = null
    private val listener = context as? Listener

    companion object {
        const val RESTAURANT = 0
        const val MORE_ITEMS = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MORE_ITEMS -> {
                val itemView = LayoutInflater.from(context).inflate(R.layout.view_load_more_items,
                    parent,
                    false)
                itemView.setOnClickListener(this)
                MoreItemViewHolder(itemView)
            }
            else -> {
                RestaurantViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.view_restaurant_grid,
                        parent,
                        false))
            }
        }
    }

    override fun getItemCount(): Int {
        return if (restaurants == null) {
            0
        } else {
            restaurants!!.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(restaurants!![position].type == Type.MORE_RESTAURANTS) {
            MORE_ITEMS
        } else {
            RESTAURANT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MoreItemViewHolder) {
            holder.setData(restaurants!![position])
        } else {
            (holder as RestaurantViewHolder).setData(restaurants!![position])
        }
    }

    override fun onClick(v: View?) {
        listener?.onMoreItemClick()
    }

    fun setAdapterData(restaurants: List<RestaurantObject>?) {
        this.restaurants = restaurants
    }

}