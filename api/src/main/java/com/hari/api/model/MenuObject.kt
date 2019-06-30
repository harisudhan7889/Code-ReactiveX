package com.hari.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author Hari Hara Sudhan.N
 */
data class MenuObject(@SerializedName("daily_menu") val menuList: List<Menu>)