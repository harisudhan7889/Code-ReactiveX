package com.hari.api.model

import com.google.gson.annotations.SerializedName

/**
 * @author Hari Hara Sudhan.N
 */
data class UserReviewsObject(@SerializedName("reviews_count") val reviewsCount: Int,
                             @SerializedName("reviews_shown") val reviewsShown: Int,
                             @SerializedName("user_reviews") val userReviews: List<UserReviews>?)