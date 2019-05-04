package com.hari.transformoperators.network

import com.hari.transformoperators.model.Restaurants
import com.hari.transformoperators.model.UserReviewsObject
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Hari Hara Sudhan.N
 */
interface ApiEndPoint {
    @GET("api/v2.1/search")
    fun getRestaurantsAtLocation(@Query("lat") lat: Double, @Query("lon") lon: Double,
                                 @Query("start") start: Int, @Query("count") count: Int): Observable<Restaurants>

    @GET("api/v2.1/reviews")
    fun getRestaurantReview(@Query("res_id") restaurantId: String,
                            @Query("start") start: Int, @Query("count") count: Int): Observable<UserReviewsObject>
}