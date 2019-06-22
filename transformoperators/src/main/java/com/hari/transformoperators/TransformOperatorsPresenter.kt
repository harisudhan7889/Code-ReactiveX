package com.hari.transformoperators

import android.app.ProgressDialog
import android.content.Context
import android.text.TextUtils
import com.hari.api.model.*
import com.hari.api.network.Api
import com.hari.api.network.ApiEndPoint
import com.hari.transformoperators.model.Bike
import com.hari.transformoperators.model.Car
import com.hari.transformoperators.model.Vehicle
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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

    fun flatMapMaybe(latitude: Double, longitude: Double) {
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val observable = endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 3)
        observable
                .flatMapIterable{it.restaurants}
                .flatMapMaybe(object : Function<RestaurantObject, MaybeSource<List<UserReviews>>> {
                    override fun apply(restaurantObject: RestaurantObject): MaybeSource<List<UserReviews>> {
                        val maybe = endPoint.getRestaurantReviewMaybe(restaurantObject.restaurant.id, 0, 3)
                        val userReview = maybe.blockingGet()
                        return if(userReview.reviewsCount > 0) {
                            Maybe.just(userReview.userReviews)
                        } else {
                            Maybe.empty()
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<UserReviews>> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("flatMapMaybe onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("flatMapMaybe onSubscribe")
                    }

                    override fun onNext(t: List<UserReviews>) {
                        System.out.println("flatMapMaybe onNext ${t.count()}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("flatMapMaybe onError $error")
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

    fun windowBasedOnSize() {
        var count = 1
        Observable.just("1", "2", "3", "4", "5")
                .window(2)
                .flatMap {
                    System.out.println("WindowObservable $count")
                    count++
                    it
                }
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        System.out.println("Window onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("Window onSubscribe")
                    }

                    override fun onNext(result: String) {
                        System.out.println("Window onNext $result")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("Window onError $error")
                    }
                })
    }

    fun windowBasedOnTime() {
        var count = 1
        Observable.just("1", "2", "3", "4", "5", "6", "7", "8")
                .window(2, TimeUnit.MILLISECONDS)
                .flatMap {
                    System.out.println("WindowObservable $count")
                    count++
                    it
                }
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        System.out.println("Window onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("Window onSubscribe")
                    }

                    override fun onNext(result: String) {
                        System.out.println("Window onNext $result")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("Window onError $error")
                    }
                })
    }

    fun windowSkip() {
        var count = 1
        Observable.just("1", "2", "3", "4", "5", "6", "7", "8")
                .window(2, 3)
                .flatMap {
                    System.out.println("WindowObservable $count")
                    count++
                    it
                }
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        System.out.println("Window onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("Window onSubscribe")
                    }

                    override fun onNext(result: String) {
                        System.out.println("Window onNext $result")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("Window onError $error")
                    }
                })
    }

    fun cast() {
        Observable.fromIterable(getManufacturedVehicles())
                .filter {
                    it is Car
                }
                .subscribe(object : Observer<Vehicle> {
                    override fun onComplete() {
                        System.out.println("cast onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("cast onSubscribe")
                    }

                    override fun onNext(result: Vehicle) {
                        result as Car
                        System.out.println("cast onNext ${result.name}")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("cast onError $error")
                    }
                })
    }

    fun flatMapSingleWithObservable() {
        val location = ArrayList<String>()
        location.add("9.925201,78.119774")
        location.add("13.082680,80.270721")
        location.add("10.073132,78.780151")
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        Observable.fromIterable(location)
                .flatMapSingle(object : Function<String, Single<Restaurants>> {
                    override fun apply(locCoordinates: String): Single<Restaurants> {
                        val locCoordArray = TextUtils.split(locCoordinates, ",")
                        return endPoint.getRestaurantsAtLocationSingle(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("flatMapSingle  onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("flatMapSingle  onSubscribe")
                    }

                    override fun onNext(t: Restaurants) {
                        System.out.println("flatMapSingle  onNext ${t.restaurants}")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("flatMapSingle  onNext $e")
                    }

                })
    }

    fun concatMapEager() {
        val location = ArrayList<String>()
        location.add("9.925201,78.119774")
        location.add("13.082680,80.270721")
        location.add("10.800820,78.689919")
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        Observable.fromIterable(location)
                .concatMapEager(object : Function<String, Observable<Restaurants>> {
                    override fun apply(locCoordinates: String): Observable<Restaurants> {
                        val locCoordArray = TextUtils.split(locCoordinates, ",")
                        return endPoint.getRestaurantsAtLocation(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("concatMapEager onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("concatMapEager onSubscribe")
                    }

                    override fun onNext(restaurants: Restaurants) {
                        val locationDetails = restaurants.restaurants[0].restaurant.location
                        System.out.println("concatMapEager onNext $locationDetails")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("concatMapEager onError $e")
                    }
                })
    }

    fun concatMapEagerDelayError() {
        val location = ArrayList<String>()
        location.add("9.925201,78.119774")
        location.add("13.082680,80.270721")
        location.add("10.800820,78.689919")
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        Observable.fromIterable(location)
                .concatMapEagerDelayError(object : Function<String, Observable<Restaurants>> {
                    override fun apply(locCoordinates: String): Observable<Restaurants> {
                        val locCoordArray = TextUtils.split(locCoordinates, ",")
                        return if (locCoordArray[0] == "13.082680" || locCoordArray[0] == "9.925201") {
                            endPoint.getRestaurantsAtLocationWithError(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                        } else {
                            endPoint.getRestaurantsAtLocation(locCoordArray[0].toDouble(), locCoordArray[1].toDouble(), 0, 3)
                        }
                    }
                }, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("concatMapEagerDelayError onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("concatMapEagerDelayError onSubscribe")
                    }

                    override fun onNext(restaurants: Restaurants) {
                        val locationDetails = restaurants.restaurants[0].restaurant.location
                        System.out.println("concatMapEagerDelayError onNext $locationDetails")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("concatMapEagerDelayError onError $e")
                    }
                })
    }

    private fun getManufacturedVehicles(): List<Vehicle> {
        val vehicles = ArrayList<Vehicle>()

        val bike1 = Bike(true, "Yamaha FZ")
        val bike2 = Bike(false, "MV Agusta F4")
        val bike3 = Bike(false, "Harley-Davidson")
        val bike4 = Bike(true, "Ducati Diavel")
        vehicles.add(bike1)
        vehicles.add(bike2)
        vehicles.add(bike3)
        vehicles.add(bike4)

        val car1 = Car(true, "Ferrari")
        val car2 = Car(false, "Lamborghini")
        val car3 = Car(true, "Rolls Royce")
        val car4 = Car(false, "Porsche")
        vehicles.add(car1)
        vehicles.add(car2)
        vehicles.add(car3)
        vehicles.add(car4)
        return vehicles
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