package com.hari.transformoperators.model

import com.google.gson.annotations.SerializedName

/**
 * @author Hari Hara Sudhan.N
 */
data class Restaurant(val id: String,
                      val name: String,
                      @SerializedName("featured_image") val thumbnailUrl: String,
                      val cuisines: String)