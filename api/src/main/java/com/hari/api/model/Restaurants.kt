package com.hari.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author Hari Hara Sudhan.N
 */
data class Restaurants(@SerializedName("restaurants") var restaurants: List<RestaurantObject>)