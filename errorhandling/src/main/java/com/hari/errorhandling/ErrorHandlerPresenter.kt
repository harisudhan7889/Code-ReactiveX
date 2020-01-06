package com.hari.errorhandling

import android.app.ProgressDialog
import android.content.Context
import com.hari.api.model.Restaurants
import com.hari.api.network.Api
import com.hari.api.network.ApiEndPoint
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.lang.annotation.AnnotationFormatError
import java.net.UnknownHostException

/**
 * @author Hari Hara Sudhan.N
 */
class ErrorHandlerPresenter(private val context: Context)  {

    fun onExceptionResumeNext() {
        Observable.just(1, 2, 3)
                .doOnNext {
                    if (it == 2) {
                        throw RuntimeException("Exception on $it")
                    }
                }.onExceptionResumeNext(Observable.just(10))
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("ExceptionResumeNext onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("ExceptionResumeNext onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("ExceptionResumeNext $value")
                    }

                    override fun onError(e: Throwable) {
                        println("ExceptionResumeNext onError $e")
                    }
                })
    }

    fun onErrorResumeNext() {
        Observable.just(1, 2, 3)
                .doOnNext {
                    if (it == 2) {
                        throw AnnotationFormatError("Error")
                    }
                }.onErrorResumeNext(Observable.just(10))
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("ExceptionResumeNext onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("ExceptionResumeNext onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("ExceptionResumeNext $value")
                    }

                    override fun onError(e: Throwable) {
                        println("ExceptionResumeNext onError $e")
                    }

                })
    }

    fun onErrorResumeNextInsideMap() {
        Observable.just(1, 2, 3, 5, 6)
                .switchMap {
                    Observable.just(it).doOnNext { value ->
                        if (value == 2) {
                            throw AnnotationFormatError("Error")
                        }
                    }.onErrorResumeNext(Observable.just(23))
                }
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("ExceptionResumeNext onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("ExceptionResumeNext onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("ExceptionResumeNext $value")
                    }

                    override fun onError(e: Throwable) {
                        println("ExceptionResumeNext onError $e")
                    }

                })
    }

    fun onErrorReturn() {
        Observable.just(1, 2, 3)
                .doOnNext {
                    if (it == 2) {
                        throw AnnotationFormatError("Error")
                    }
                }.onErrorReturn(object : Function<Throwable, Int>{
                override fun apply(error: Throwable): Int {
                    print(error.message)
                    return if(error is AnnotationFormatError) {
                        -1
                    } else {
                        -2
                    }
                }
            })
            .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("onErrorReturn onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onErrorReturn onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("onErrorReturn $value")
                    }

                    override fun onError(e: Throwable) {
                        println("onErrorReturn onError $e")
                    }
                })
    }

    fun onErrorReturnItem() {
        Observable.just(1, 2, 3)
                .doOnNext {
                    if (it == 2) {
                        throw AnnotationFormatError("Error")
                    }
                }.onErrorReturnItem(-1)
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        println("onErrorReturnItem onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onErrorReturnItem onSubscribe")
                    }

                    override fun onNext(value: Int) {
                        println("onErrorReturnItem $value")
                    }

                    override fun onError(e: Throwable) {
                        println("onErrorReturnItem onError $e")
                    }

                })
    }

    fun retryWhen() {
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

        val observable2 = endPoint.getRestaurantsAtLocation(secondLocation[0], secondLocation[1], 0, 3)

        val observable3 = endPoint.getRestaurantsAtLocation(thirdLocation[0], thirdLocation[1], 0, 3).doOnNext {
            throw UnknownHostException()
        }

        observable1
                .mergeWith(observable2)
                .mergeWith(observable3)
                .retryWhen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Restaurants> {
                    override fun onComplete() {
                        progressBar.dismiss()
                        println("retryWhen onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        println("retryWhen onSubscribe")
                    }

                    override fun onNext(restaurants: Restaurants) {
                        println("retryWhen onNext ${restaurants.restaurants.size}")
                    }

                    override fun onError(error: Throwable) {
                        progressBar.dismiss()
                        println("retryWhen onError $error")
                    }

                })
    }

    private fun Observable<Restaurants>.retryWhen(): Observable<Restaurants> {
        return retryWhen { errors ->
            errors.zipWith(Observable.range(1, 3), object: BiFunction<Throwable, Int, Boolean>{
                override fun apply(error: Throwable, count: Int): Boolean {
                    println("retryWhen count $count")
                    return error is UnknownHostException
                }
            })
        }
    }
}