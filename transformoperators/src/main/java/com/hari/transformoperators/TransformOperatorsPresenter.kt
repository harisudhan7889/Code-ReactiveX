package com.hari.transformoperators

import android.app.ProgressDialog
import android.content.Context
import com.hari.transformoperators.model.RestaurantObject
import com.hari.transformoperators.model.UserReviews
import com.hari.transformoperators.model.UserReviewsObject
import com.hari.transformoperators.network.Api
import com.hari.transformoperators.network.ApiEndPoint
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * @author Hari Hara Sudhan.N
 */
class TransformOperatorsPresenter(private val context: Context) {

    fun buffer() {
        Observable.just("1", "2", "3", "4", "5")
            .buffer(2)
            .subscribe(object : Observer<List<String>>{
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(t: List<String>) {
                    System.out.println("onNext: ")
                    t.forEach {
                        System.out.println(it)
                    }
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }

            })
    }

    fun map() {
        val valueArray: Array<Int> = arrayOf(1, 2, 3, 4, 5, 6)
        Observable.fromArray(*valueArray)
            .map(object : Function<Int, String>{
                override fun apply(value: Int): String {
                    return if (value % 2 == 0) {
                        "$value is a Even Number"
                    } else {
                        "$value is a Odd Number"
                    }
                }
            })
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(value: String) {
                    System.out.println("onNext: $value")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flatMap(latitude: Double, longitude: Double) {
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 3)
        observable
            .flatMap { Observable.fromIterable(it.restaurants) }
            .flatMap(object : Function<RestaurantObject, Observable<UserReviewsObject>>{
            override fun apply(restaurantObject: RestaurantObject): Observable<UserReviewsObject> {
               return endPoint.getRestaurantReview(restaurantObject.restaurant.id, 0, 3)
            }})
            .map(object : Function<UserReviewsObject, List<UserReviews>>{
            override fun apply(userReviewsObject: UserReviewsObject): List<UserReviews>? {
                return userReviewsObject.userReviews
            }})
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<UserReviews>?> {
                override fun onComplete() {
                    System.out.println("FlatMap onComplete")
                    progressBar.dismiss()
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("FlatMap onSubscribe")
                    progressBar.show()
                }

                override fun onNext(userReviews: List<UserReviews>) {
                    System.out.println("FlatMap onNext No of Reviews: ${userReviews.size}")
                }

                override fun onError(error: Throwable) {
                    System.out.println("FlatMap onError $error")
                }
            })
    }
}