package com.hari.api.network

import com.hari.api.model.Restaurants
import com.hari.api.model.UserReviewsObject
import io.reactivex.Maybe
import io.reactivex.MaybeSource
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author Hari Hara Sudhan.N
 */
interface ApiEndPoint {

    @GET("api/v2.1/search")
    fun getRestaurantsAtLocation(@Query("lat") lat: Double, @Query("lon") lon: Double,
                                 @Query("start") start: Int, @Query("count") count: Int): Observable<Restaurants>

    @GET("api/v2.1/search1")
    fun getRestaurantsAtLocationWithError(@Query("lat") lat: Double, @Query("lon") lon: Double,
                                 @Query("start") start: Int, @Query("count") count: Int): Observable<Restaurants>

    @GET("api/v2.1/reviews")
    fun getRestaurantReview(@Query("res_id") restaurantId: String,
                            @Query("start") start: Int, @Query("count") count: Int): Observable<UserReviewsObject>

    @GET("api/v2.1/search")
    fun getRestaurantsAtLocationSingle(@Query("lat") lat: Double, @Query("lon") lon: Double,
                                 @Query("start") start: Int, @Query("count") count: Int): Single<Restaurants>


    @GET("api/v2.1/search")
    fun getRestaurantsAtLocationMaybe(@Query("lat") lat: Double, @Query("lon") lon: Double,
                                       @Query("start") start: Int, @Query("count") count: Int): Maybe<Restaurants>

    @GET("api/v2.1/reviews")
    fun getRestaurantReviewMaybe(@Query("res_id") restaurantId: String,
                            @Query("start") start: Int, @Query("count") count: Int): Maybe<UserReviewsObject>
}