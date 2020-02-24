package com.hari.filteroperators

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Predicate
import java.util.concurrent.TimeUnit


/**
 * @author Hari Hara Sudhan.N
 */
class FilterOperatorPresenter {

    private val observer = object : Observer<Int> {
        override fun onComplete() {
            println("onComplete")
        }

        override fun onSubscribe(d: Disposable) {
            println("onSubscribe")
        }

        override fun onNext(output: Int) {
            println("onNext $output")
        }

        override fun onError(e: Throwable) {
            println("onError")
        }
    }

    fun distinctOperator() {
        Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .distinct()
            .subscribe(observer)
    }

    fun elementAtOperator() {
        Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .elementAt(3)
            .subscribe(object : MaybeObserver<Int>{
                override fun onSuccess(value: Int) {
                    println("onSuccess $value")
                }

                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(error: Throwable) {
                    println("onError ${error.message}")
                }
            })
    }

    fun elementAtOrErrorOperator() {
        Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .elementAtOrError(10)
            .subscribe(object : SingleObserver<Int>{
                override fun onError(error: Throwable) {
                    println("onError $error")
                }

                override fun onSuccess(value: Int) {
                    println("onSuccess $value")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }
            })
    }

    fun elementAtWithDefaultValue() {
        Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .elementAt(10, 90)
            .subscribe(object : SingleObserver<Int>{
                override fun onError(error: Throwable) {
                    println("onError $error")
                }

                override fun onSuccess(value: Int) {
                    println("onSuccess $value")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }
            })
    }

    fun filter() {
        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .filter(object : Predicate<Int> {
                override fun test(input: Int): Boolean {
                    return (input % 3 == 0)
                }
            }).subscribe(object: Observer<Int> {
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

    fun ignoreElements() {
        Observable.range(0, 10)
            .ignoreElements()
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }

    fun skip() {
        Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .skip(4)
            .subscribe(object: Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: String) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }

    fun skipLast() {
        Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .skipLast(4)
            .subscribe(object: Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: String) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }

    fun take() {
        Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .take(4)
            .subscribe(object: Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: String) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }

    fun takeLast() {
        Observable.just("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
            .takeLast(4)
            .subscribe(object: Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(value: String) {
                    println("onNext $value")
                }

                override fun onError(error: Throwable) {
                    println("onError $error")
                }
            })
    }

}