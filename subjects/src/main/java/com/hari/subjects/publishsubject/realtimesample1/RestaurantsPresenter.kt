package com.hari.subjects.publishsubject.realtimesample1

import com.hari.api.model.Restaurant
import com.hari.api.model.RestaurantObject
import com.hari.api.model.Restaurants
import com.hari.api.model.Type
import com.hari.api.network.Api
import com.hari.api.network.ApiEndPoint
import com.hari.subjects.SubjectApplication
import com.hari.subjects.common.model.RestaurantCounterWithLocation
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @author Hari Hara Sudhan.N
 */
class RestaurantsPresenter {

    private lateinit var locationWithRestaurantCounter: RestaurantCounterWithLocation

    private fun getRestaurants() {
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        endPoint.getRestaurantsAtLocationSingle(
            locationWithRestaurantCounter.latitude.toDouble(),
            locationWithRestaurantCounter.latitude.toDouble(),
            0,
            locationWithRestaurantCounter.restaurantCounter
        ).map {
            (it.restaurants as ArrayList).add(
                RestaurantObject(
                    Type.MORE_RESTAURANTS,
                    Restaurant("")
                )
            )
            it
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Restaurants> {
                override fun onSuccess(restaurants: Restaurants) {
                    SubjectApplication.getSubjectApplication()?.retrieveLoadRestaurantState()?.publish(restaurants)
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun loadRestaurantsForFirsTime(latitude: String, longitude: String) {
        locationWithRestaurantCounter = RestaurantCounterWithLocation(3, latitude, longitude)
        getRestaurants()
    }

    fun loadMoreRestaurants() {
        locationWithRestaurantCounter.restaurantCounter =
            locationWithRestaurantCounter.restaurantCounter.plus(3)
        getRestaurants()
    }

}