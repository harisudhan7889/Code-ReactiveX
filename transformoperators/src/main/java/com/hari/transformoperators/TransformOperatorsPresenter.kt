package com.hari.transformoperators

import android.app.ProgressDialog
import android.content.Context
import com.hari.api.model.RestaurantObject
import com.hari.api.model.UserReviews
import com.hari.api.model.UserReviewsObject
import com.hari.api.model.WinningCount
import com.hari.api.network.Api
import com.hari.api.network.ApiEndPoint
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

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

    fun switchMap() {
        val valueArray: Array<Int> = arrayOf(1, 2, 3, 4, 5, 6)
        Observable.fromArray(*valueArray)
            .switchMap(object : Function<Int, Observable<String>> {
                override fun apply(t: Int): Observable<String> {
                    val value = if (t % 2 == 0) {
                        "$t is a Even Number"
                    } else {
                        "$t is a Odd Number"
                    }
                    return Observable.just(value).delay(1, TimeUnit.SECONDS)
                }
            }).subscribe(object : Observer<String> {
                override fun onComplete() {
                    System.out.println("switchMap onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("switchMap onSubscribe")
                }

                override fun onNext(value: String) {
                    System.out.println("switchMap onNext: $value")
                }

                override fun onError(error: Throwable) {
                    System.out.println("switchMap onError $error")
                }
            })
    }

    fun groupBy(latitude: Double, longitude: Double) {
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 20)
        val cuisineAsian = "Asian"
        val cuisineAmerican = "American"
        val cuisineItalian = "Italian"
        val cuisineOthers = "Other"
        val subscribe = observable
            .flatMap { Observable.fromIterable(it.restaurants) }
            .groupBy(object : Function<RestaurantObject, String> {
                override fun apply(restaurantObject: RestaurantObject): String {
                    return when {
                        restaurantObject.restaurant.cuisines.contains("Asian", true) -> cuisineAsian
                        restaurantObject.restaurant.cuisines.contains("American", true) -> cuisineAmerican
                        restaurantObject.restaurant.cuisines.contains("Italian", true) -> cuisineItalian
                        else -> cuisineOthers
                    }
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { progressBar.show() }
            .doOnComplete { progressBar.dismiss() }
            .doOnError { progressBar.dismiss() }
            .subscribe {
                when (it.key) {
                    cuisineAsian -> {
                        it.subscribe {
                            System.out.println("Asian Food available at ${it.restaurant.name}")
                        }
                    }
                    cuisineAmerican -> {
                        it.subscribe {
                            System.out.println("American Food available at ${it.restaurant.name}")
                        }
                    }
                    cuisineItalian -> {
                        it.subscribe {
                            System.out.println("Italian Food available at ${it.restaurant.name}")
                        }
                    }
                }
            }
    }

    fun scan1() {
        Observable.just(1,2,3,4,5)
            .scan(object : BiFunction<Int, Int, Int>{
                override fun apply(t1: Int, t2: Int): Int {
                    return t1 + t2
                }
            })
            .subscribe(object : Observer<Int>{
                override fun onComplete() {
                    System.out.println("scan1 onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("scan1 onSubscribe")
                }

                override fun onNext(result: Int) {
                    System.out.println("scan1 onNext $result")
                }

                override fun onError(e: Throwable) {
                    System.out.println("scan1 onError $e")
                }
            })
    }

    fun scan2() {

        val countries = getWorldCupWinners()

        Observable.fromIterable(countries)
            .scan(WinningCount(""), object : BiFunction<WinningCount, WinningCount, WinningCount> {
                override fun apply(accumulator: WinningCount, newObject: WinningCount): WinningCount {
                    if (accumulator.winningCounts.containsKey(newObject.countryName)) {
                        val winningCount = accumulator.winningCounts[newObject.countryName] ?: 0
                        accumulator.winningCounts[newObject.countryName] = winningCount.inc()
                    } else {
                        accumulator.winningCounts[newObject.countryName] = 1
                    }
                    return accumulator
                }
            })
            .subscribe(object : Observer<WinningCount> {
                override fun onComplete() {
                    System.out.println("scan2 onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("scan2 onSubscribe")
                }

                override fun onNext(t: WinningCount) {
                    t.winningCounts.forEach {
                        System.out.println("scan2 onNext: No of times ${it.key} had won the Cricket World Cup is ${it.value}")
                    }
                }

                override fun onError(e: Throwable) {
                    System.out.println("scan2 onError $e")
                }
            })
    }

    fun reduce() {
        val countries = getWorldCupWinners()
        Observable.fromIterable(countries)
            .reduce(WinningCount(""), object : BiFunction<WinningCount, WinningCount, WinningCount> {
                override fun apply(accumulator: WinningCount, newObject: WinningCount): WinningCount {
                    if (accumulator.winningCounts.containsKey(newObject.countryName)) {
                        val winningCount = accumulator.winningCounts[newObject.countryName] ?: 0
                        accumulator.winningCounts[newObject.countryName] = winningCount.inc()
                    } else {
                        accumulator.winningCounts[newObject.countryName] = 1
                    }
                    return accumulator
                }
            })
            .subscribe(object : SingleObserver<WinningCount> {
                override fun onSuccess(accumulator: WinningCount) {
                    accumulator.winningCounts.forEach {
                        System.out.println("reduce onSuccess: No of times ${it.key} had won the Cricket World Cup is ${it.value}")
                    }
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("reduce onSubscribe")
                }

                override fun onError(e: Throwable) {
                    System.out.println("reduce onError $e")
                }

            })
    }

    private fun getWorldCupWinners(): MutableList<WinningCount> {
        val countries = mutableListOf<WinningCount>()
        val valueArray: Array<String> = arrayOf(
            "West Indies",
            "West Indies",
            "India",
            "Australia",
            "Pakistan",
            "Sri Lanka",
            "Australia",
            "Australia",
            "Australia",
            "India",
            "Australia"
        )

        for (i in 0 until valueArray.size - 1) {
            countries.add(WinningCount(valueArray[i]))
        }
        return countries
    }
}