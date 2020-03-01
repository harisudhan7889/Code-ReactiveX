package com.hari.subjects.publishsubject

import com.hari.api.model.Restaurant
import com.hari.api.model.RestaurantObject
import com.hari.api.model.Restaurants
import com.hari.api.model.Type
import com.hari.api.network.Api
import com.hari.api.network.ApiEndPoint
import com.hari.subjects.common.model.RestaurantCounterWithLocation
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * @author Hari Hara Sudhan.N
 */
class PublishSubjectPresenter {

    private var locationWithRestaurantCounter: RestaurantCounterWithLocation? = null
    private val loadRestaurantPublishSubject = BehaviorSubject.create<RestaurantCounterWithLocation>()

    fun getRestaurants(): Observable<Restaurants> {
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        return loadRestaurantPublishSubject
            .observeOn(Schedulers.io())
            .flatMapSingle {
                println("PublishSubject Flatmapsingle")
                endPoint.getRestaurantsAtLocationSingle(
                    it.latitude.toDouble(),
                    it.latitude.toDouble(),
                    0,
                    it.restaurantCounter
                )
            }.map {
                println("PublishSubject map")
                (it.restaurants as ArrayList).add(RestaurantObject(Type.MORE_RESTAURANTS, Restaurant("")))
                it
            }
    }

    fun loadRestaurantsForFirsTime(latitude: String, longitude: String) {
        locationWithRestaurantCounter = RestaurantCounterWithLocation(3, latitude, longitude)
        loadRestaurantPublishSubject.onNext(locationWithRestaurantCounter!!)
    }

    fun loadMoreRestaurants() {
        locationWithRestaurantCounter?.restaurantCounter =
            locationWithRestaurantCounter?.restaurantCounter?.plus(3) ?: 0
        loadRestaurantPublishSubject.onNext(locationWithRestaurantCounter!!)
    }

}