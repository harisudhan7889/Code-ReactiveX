package com.hari.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author Hari Hara Sudhan.N
 */
class Dish(@SerializedName("dish_id") val dishId: String, val name: String, val price: String)