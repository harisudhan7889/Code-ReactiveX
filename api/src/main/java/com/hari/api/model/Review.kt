package com.hari.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author Hari Hara Sudhan.N
 */
data class Review(val rating: String,
                  @SerializedName("review_text") val reviewText: String,
                  val id: Int)