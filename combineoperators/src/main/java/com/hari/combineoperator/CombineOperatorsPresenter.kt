package com.hari.combineoperator

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import com.hari.api.model.MenuObject
import com.hari.api.model.RestaurantObject
import com.hari.api.model.Restaurants
import com.hari.api.model.UserReviewsObject
import com.hari.api.network.Api
import com.hari.api.network.ApiEndPoint
import com.hari.api.utils.AppUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * @author Hari Hara Sudhan.N
 */
class CombineOperatorsPresenter(private val context: Context) {

    fun mergeWith() {
        val firstLocation = DoubleArray(2)
        firstLocation[0] = 9.925201
        firstLocation[1] = 78.119774

        val secondLocation = DoubleArray(2)
        secondLocation[0] = 13.082680
        secondLocation[1] = 80.270721

        val thirdLocation = DoubleArray(2)
        secondLocation[0] = 10.800820
        secondLocation[1] = 78.689919

        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)

        val observable1 = endPoint.getRestaurantsAtLocation(firstLocation[0], firstLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        val observable2 = endPoint.getRestaurantsAtLocation(secondLocation[0], secondLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        val observable3 = endPoint.getRestaurantsAtLocation(thirdLocation[0], thirdLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        observable1.mergeWith(observable2)
                .mergeWith(observable3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("mergeWith onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("mergeWith onSubscribe")
                    }

                    override fun onNext(restaurants: Restaurants) {
                        System.out.println("mergeWith onNext ${restaurants.restaurants.size}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("mergeWith onError $error")
                    }

                })
    }

    fun mergeDelayError() {
        val firstLocation = DoubleArray(2)
        firstLocation[0] = 9.925201
        firstLocation[1] = 78.119774

        val secondLocation = DoubleArray(2)
        secondLocation[0] = 13.082680
        secondLocation[1] = 80.270721

        val thirdLocation = DoubleArray(2)
        secondLocation[0] = 10.800820
        secondLocation[1] = 78.689919

        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)

        val observable1 = endPoint.getRestaurantsAtLocationWithError(firstLocation[0], firstLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        val observable2 = endPoint.getRestaurantsAtLocationWithError(secondLocation[0], secondLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        val observable3 = endPoint.getRestaurantsAtLocation(thirdLocation[0], thirdLocation[1], 0, 3)
                .subscribeOn(Schedulers.io())

        Observable.mergeDelayError(observable1, observable2, observable3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("mergeDelayError onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("mergeDelayError onSubscribe")
                    }

                    override fun onNext(restaurants: Restaurants) {
                        System.out.println("mergeDelayError onNext ${restaurants.restaurants.size}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("mergeDelayError onError $error")
                    }
                })
    }

    fun zip(latitude: Double, longitude: Double) {
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        endPoint.getRestaurantsAtLocation(latitude, longitude, 0, 5)
                .subscribeOn(Schedulers.io())
                .flatMapIterable { it.restaurants }
                .concatMapEagerDelayError(object : Function<RestaurantObject,Observable<RestaurantObject>>{
                    override fun apply(restaurantObject: RestaurantObject): Observable<RestaurantObject> {

                        val reviewObservable = endPoint.getRestaurantReview(restaurantObject.restaurant.id, 0, 3)
                                .subscribeOn(Schedulers.io())
                        val menuObservable = endPoint.getRestaurantDailyMenu(restaurantObject.restaurant.id)
                                .subscribeOn(Schedulers.io())

                        return Observable.zip(reviewObservable, menuObservable, object : BiFunction<UserReviewsObject, MenuObject, RestaurantObject> {
                            override fun apply(review: UserReviewsObject, menu: MenuObject): RestaurantObject {
                                restaurantObject.restaurant.review = review
                                restaurantObject.restaurant.menu = menu
                                return restaurantObject
                            }
                        })
                    }
                }, true)
                .observeOn(AndroidSchedulers.mainThread(), true)
                .subscribe(object : Observer<RestaurantObject> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("zip onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("zip onSubscribe")
                    }

                    override fun onNext(restaurant: RestaurantObject) {
                        System.out.println("zip onNext "+restaurant.restaurant.name)
                        System.out.println("zip onNext "+restaurant.restaurant.review?.userReviews?.size)
                        System.out.println("zip onNext "+restaurant.restaurant.menu?.menuList?.size)
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        System.out.println("zip onError $error")
                    }
                })
    }

    @SuppressLint("CheckResult")
    fun combineLatest(userNameObservable: Observable<CharSequence>,
                      passwordObservable: Observable<CharSequence>,
                      signUpButtonConsumer: Consumer<in Boolean>,
                      errorConsumer: Consumer<Throwable>) {
        Observable.combineLatest(userNameObservable, passwordObservable, object : BiFunction<CharSequence, CharSequence, Boolean> {
            @Throws(Exception::class)
            override fun apply(userName: CharSequence, password: CharSequence): Boolean {
                System.out.println("combineLatest UserName : $userName and Password : $password")
                return AppUtils.isValidMail(userName) && AppUtils.isValidPassword(password)
            }
        })
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(signUpButtonConsumer, errorConsumer)
    }

    fun join(latitude: Double, longitude: Double) {
        //Todo Yet to implement
    }

    fun startWith() {
        //Todo Yet to implement
    }
}