package com.hari.rxjava.operators.basic

import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable


/**
 * @author Hari Hara Sudhan.N
 */
class BasicOperatorsPresenterImpl(private val observer: Observer<String?>) {

    /*RxJava Create operation*/
    fun create(input: String) {
        if (input.isNotEmpty()) {
            val valueArray = TextUtils.split(input, ",")

            val observable = Observable.create(ObservableOnSubscribe<String> {
                // User provided function
                try {
                    for (value in valueArray) {
                        it.onNext(value)
                    }
                    it.onComplete()
                } catch (e: Exception) {
                    it.onError(e)
                }
            })

            observable.subscribe(observer)
        }
    }


    fun just(input: String) {
        Observable.just(input).subscribe(observer)
    }

    /* Deferring Observable code until subscription */
    fun defer() {
        var input: String? = "null"
        val just = Observable.just(input)
        input = "123"
        just.subscribe(observer)

        input = "null"
        val defer = Observable.defer { Observable.just(input) }
        input = "123"
        defer.subscribe(observer)
    }


    fun fromArray(input: String) {
        if (input.isNotEmpty()) {
            val valueArray: Array<String> = TextUtils.split(input, ",")
            Observable.fromArray(*valueArray)
                .subscribe(observer)
        }
    }

    fun fromCallable() {
        Observable.fromCallable(object : Callable<String> {
            override fun call(): String {
                return getUserDetailFromDB()
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    /*Time consuming task. This can be any Network call, DB updating call, etc*/
    @Throws(InterruptedException::class)
    fun getUserDetailFromDB(): String {
        Thread.sleep(10000)
        return "User1"
    }

    fun fromIterable(input: String) {
        if (input.isNotEmpty()) {
            val valueArray: Array<String> = TextUtils.split(input, ",")
            val list = ArrayList<String>()
            valueArray.forEach {
                list.add(it)
            }
            Observable.fromIterable(list)
                .subscribe(observer)
        }
    }
}