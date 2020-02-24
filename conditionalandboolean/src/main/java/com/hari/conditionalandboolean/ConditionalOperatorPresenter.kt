package com.hari.conditionalandboolean

import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @author Hari Hara Sudhan.N
 */
class ConditionalOperatorPresenter {
    fun all() {
        Observable.just(0, 1, 2, 3, 4, 0, 6, 0)
            .all { item -> item > 0 }
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSuccess(isConditionSatisfied: Boolean) {
                    println("onSuccess $isConditionSatisfied")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(e: Throwable) {
                    println("onError ${e.message}")
                }
            })
    }

    fun amb() {
        val observable1 = Observable.timer(3, TimeUnit.SECONDS).flatMap {
            Observable.just(1, 2, 3)
        }
        val observable2 = Observable.timer(2, TimeUnit.SECONDS).flatMap {
            Observable.just(4, 5, 6)
        }

        Observable.amb(listOf(observable1, observable2)).subscribe(object : Observer<Int> {
            override fun onComplete() {
                println("onComplete")
            }

            override fun onSubscribe(d: Disposable) {
                println("onSubscribe")
            }

            override fun onNext(value: Int) {
                println("onNext $value")
            }

            override fun onError(e: Throwable) {
                println("onError ${e.message}")
            }
        })
    }

    fun contains() {
        Observable.just(0, 1, 2, 3, 4, 0, 6, 0)
            .contains(4)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSuccess(isItemAvailable: Boolean) {
                    println("onSuccess $isItemAvailable")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(e: Throwable) {
                    println("onError ${e.message}")
                }
            })
    }

    fun defaultIfEmpty() {
        Observable.just(1, 3, 5, 7, 9, 11, 13)
            .filter(object : Predicate<Int>{
                override fun test(item: Int): Boolean {
                    return (item % 2 == 0)
                }
            }).defaultIfEmpty(-1)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(e: Throwable) {
                    println("onError ${e.message}")
                }
            })
    }

    fun sequenceEqual() {
        val observable1 = Observable.just(1, 3, 5, 7, 9, 11, 13)
        val observable2 = Observable.just(1, 3, 5, 7, 9, 11, 13)
        Observable.sequenceEqual<Int>(observable1, observable2).subscribe(object : SingleObserver<Boolean> {
            override fun onSuccess(isSequenceEqual: Boolean) {
                println("Is both observerable streams are equal: $isSequenceEqual")
            }

            override fun onSubscribe(d: Disposable) {
                println("onSubscribe")
            }

            override fun onError(e: Throwable) {
                println("onError ${e.message}")
            }
        })
    }

    fun skipUntil() {
        val observable1 = Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                for (i in 0..5) {
                    Thread.sleep(1000)
                    emitter.onNext(i)
                }
                emitter.onComplete()
            }
        })

        val observable2 = Observable.timer(3, TimeUnit.SECONDS)

        observable1.skipUntil(observable2)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }

    fun takeUntil() {
        val observable1 = Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                for (i in 0..5) {
                    Thread.sleep(1000)
                    emitter.onNext(i)
                }
                emitter.onComplete()
            }
        })

        val observable2 = Observable.timer(3, TimeUnit.SECONDS)

        observable1.takeUntil(observable2)
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }

    fun skipWhile() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .skipWhile(object : Predicate<Int> {
                override fun test(emittedValue: Int): Boolean {
                    return emittedValue <= 5
                }
            }).subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }

    fun takeWhile() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .takeWhile(object : Predicate<Int> {
                override fun test(emittedValue: Int): Boolean {
                    return emittedValue <= 5
                }
            }).subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: Int) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }
}