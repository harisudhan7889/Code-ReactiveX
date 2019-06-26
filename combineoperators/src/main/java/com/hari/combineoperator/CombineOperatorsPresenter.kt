package com.hari.combineoperator

import android.app.ProgressDialog
import android.content.Context
import com.hari.api.model.Restaurants
import com.hari.api.network.Api
import com.hari.api.network.ApiEndPoint
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @author Hari Hara Sudhan.N
 */
class CombineOperatorsPresenter(private val context: Context) {

    fun startWith() {

    }

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
}