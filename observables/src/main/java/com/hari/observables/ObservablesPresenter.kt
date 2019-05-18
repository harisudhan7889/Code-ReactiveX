package com.hari.observables

import io.reactivex.BackpressureOverflowStrategy
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

class ObservablesPresenter {

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
                }

                override fun onComplete() {
                    System.out.println("onComplete")
                }

                override fun onNext(value: Int?) {
                    System.out.println("onNext $value")
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

}