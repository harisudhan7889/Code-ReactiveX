package com.hari.observables

import android.app.ProgressDialog
import android.content.Context
import com.hari.api.model.Restaurants
import com.hari.api.network.Api
import com.hari.api.network.ApiEndPoint
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber

/**
 * @author Hari Hara Sudhan.N
 */
class ObservablesPresenter(private val context: Context) {

    fun simpleObservable() {
        Observable.just(1, 2, 3)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    System.out.println("onSubscribe")
                }

                override fun onNext(result: Int) {
                    System.out.println("onNext: $result")
                }

                override fun onError(error: Throwable) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flowableWithMissingStrategy() {
        Observable.range(0, 10000)
            .toFlowable(BackpressureStrategy.MISSING)
            .observeOn(Schedulers.computation())
            .subscribe(object : DisposableSubscriber<Int>() {
                override fun onStart() {
                    System.out.println("onStart")
                    request(1)
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
                    request(1)
                }

                override fun onError(error: Throwable?) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flowableWithDropStrategy() {
        Observable.range(0, 10000)
            .toFlowable(BackpressureStrategy.DROP)
            .observeOn(Schedulers.computation())
            .subscribe(object : DisposableSubscriber<Int>() {
                override fun onStart() {
                    System.out.println("onStart")
                    request(1)
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
                    //request(1)
                }

                override fun onError(error: Throwable?) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flowableWithLatestStrategy() {
        Observable.range(0, 10000)
            .toFlowable(BackpressureStrategy.LATEST)
            .observeOn(Schedulers.computation())
            .subscribe(object : DisposableSubscriber<Int>() {
                override fun onStart() {
                    System.out.println("onStart")
                    request(1)
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
                    request(1)
                }

                override fun onError(error: Throwable?) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flowableWithErrorStrategy() {
        Observable.range(0, 10000)
            .toFlowable(BackpressureStrategy.ERROR)
            .observeOn(Schedulers.computation())
            .subscribe(object : DisposableSubscriber<Int>() {
                override fun onStart() {
                    System.out.println("onStart")
                    request(1)
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
                    request(1)
                }

                override fun onError(error: Throwable?) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flowableWithUnboundedBufferStrategy() {
        Observable.range(0, 10000)
            .toFlowable(BackpressureStrategy.MISSING)
            .onBackpressureLatest()
            .observeOn(Schedulers.computation())
            .subscribe(object : DisposableSubscriber<Int>() {
                override fun onStart() {
                    System.out.println("onStart")
                    request(1)
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
                    request(1)
                }

                override fun onError(error: Throwable?) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flowableWithBoundedBufferStrategy() {
        Observable.range(0, 10000)
            .toFlowable(BackpressureStrategy.MISSING)
            .onBackpressureBuffer(16)
            .observeOn(Schedulers.computation())
            .subscribe(object : DisposableSubscriber<Int>() {
                override fun onStart() {
                    System.out.println("onStart")
                    request(1)
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
                    request(1)
                }

                override fun onError(error: Throwable?) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flowableWithBufferOverFlowAction() {
        Observable.range(0, 10000)
            .toFlowable(BackpressureStrategy.MISSING)
            .onBackpressureBuffer(16, object: Action {
                override fun run() {
                    System.out.println("Buffer overload causes the overflowing.")
                }
            })
            .observeOn(Schedulers.computation())
            .subscribe(object : DisposableSubscriber<Int>() {
                override fun onStart() {
                    System.out.println("onStart")
                    request(1)
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
                    request(1)
                }

                override fun onError(error: Throwable?) {
                    System.out.println("onError $error")
                }
            })
    }

    fun flowableWithBufferOverFlowStrategy() {
        Observable.range(0, 10000)
            .toFlowable(BackpressureStrategy.MISSING)
            .onBackpressureBuffer(16, object: Action {
                override fun run() {
                    System.out.println("Buffer overload causes the overflowing.")
                }
            }, BackpressureOverflowStrategy.DROP_LATEST)
            .observeOn(Schedulers.computation())
            .subscribe(object : DisposableSubscriber<Int>() {
                override fun onStart() {
                    System.out.println("onStart")
                    request(1)
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
                    request(1)
                }

                override fun onError(error: Throwable?) {
                    System.out.println("onError $error")
                }
            })
    }

    fun singleObservable(latitude: Double, longitude: Double) {
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val single = endPoint.getRestaurantsAtLocationSingle(latitude, longitude, 0, 3)
        single
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Restaurants> {
                override fun onSuccess(result: Restaurants) {
                    progressBar.dismiss()
                    System.out.println("OnSuccess ${result.restaurants.size}")
                }

                override fun onSubscribe(d: Disposable) {
                    progressBar.show()
                    System.out.println("onSubscribe")
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    System.out.println("onError $e")
                }

            })
    }

    fun maybe() {
        val progressBar = ProgressDialog(context)
        val endPoint = Api.getClient().create(ApiEndPoint::class.java)
        val maybe = endPoint.getRestaurantsAtLocationMaybe(0.0, 0.0, 0, 3)
        maybe.filter { it.restaurants.isNotEmpty() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MaybeObserver<Restaurants>{

                    override fun onSuccess(result: Restaurants) {
                        System.out.println("onSuccess $result")
                    }

                    override fun onComplete() {
                        progressBar.dismiss()
                        System.out.println("onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("onSubscribe")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("onError $e")
                    }
                })
    }
}