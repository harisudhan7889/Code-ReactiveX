package com.hari.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author Hari Hara Sudhan.N
 */
class Menu(@SerializedName("daily_menu_id") val menuId: String, val name: String, val dishes: List<Dish>)