package com.hari.rxjava.operators.basic

import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BooleanSupplier
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit


/**
 * @author Hari Hara Sudhan.N
 */
class BasicOperatorsPresenterImpl(private val observer: Observer<String?>) {

    private var futureTask: FutureTask<String>? = null

    /*RxJava Create operation*/
    fun create(input: String) {
        if (input.isNotEmpty()) {
            val valueArray = TextUtils.split(input, ",")
            Observable.create(ObservableOnSubscribe<String> {
                // User provided function
                try {
                    for (value in valueArray) {
                        it.onNext(value)
                    }
                    it.onComplete()
                } catch (e: Exception) {
                    it.onError(e)
                }
            }).subscribe(observer)
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
        val observerble = defer.subscribe(observer)
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
                return getUserDetailFromRemote()
            }
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    /*Time consuming task. This can be any Network call, DB updating call, etc*/
    @Throws(InterruptedException::class)
    fun getUserDetailFromRemote(): String {
        Thread.sleep(10000)
        return "User1"
    }

    @Throws(InterruptedException::class)
    fun getUserDetailFromDB(): String {
        Thread.sleep(5000)
        return "User2"
    }

    fun fromIterable(input: String) {
        if (input.isNotEmpty()) {
            val valueArray: Array<String> = TextUtils.split(input, ",")
            val list = ArrayList<String>()
            valueArray.forEach {
                list.add(it)
            }
            val disposable = Observable.fromIterable(list)
                .subscribe(observer)
        }
    }

    fun fromFuture() {
        if (futureTask == null) {
            futureTask = FutureTask<String>(object : Callable<String> {
                override fun call(): String {
                    return getUserDetailFromRemote()
                }
            })
        }
        Observable.fromFuture(futureTask)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe {
                if (!futureTask!!.isDone) {
                    futureTask!!.run()
                }
            }
            .doOnDispose {
                if (!futureTask!!.isDone && !futureTask!!.isCancelled) {
                    futureTask!!.cancel(true)
                }
            }
            .subscribe(observer)
    }

    fun range(startNo: Int, count: Int) {
        Observable.range(startNo, count)
            .map { it.toString() }
            .subscribe(observer)
    }

    fun repeat(startNo: Int, count: Int) {
        Observable.range(startNo, count)
            .repeat()
            .take(3)
            .map { it.toString() }
            .subscribeOn(Schedulers.io())
            .subscribe(observer)
    }

    fun repeatWithLimit(startNo: Int, count: Int) {
        Observable.range(startNo, count)
            .repeat(2)
            .map { it.toString() }
            .subscribe(observer)
    }

    fun repeatUntil(startNo: Int, count: Int) {
        val startTimeMillis = System.currentTimeMillis()
        Observable.range(startNo, count)
            .repeatUntil(object : BooleanSupplier {
                override fun getAsBoolean(): Boolean {
                    return System.currentTimeMillis() - startTimeMillis > 500
                }
            }).map { it.toString() }
            .subscribe(observer)
    }

    fun repeatWhen(startNo: Int, count: Int) {
        Observable.range(startNo, count)
            .repeatWhen(object : Function<Observable<Any>, ObservableSource<Any>> {
                override fun apply(t: Observable<Any>): ObservableSource<Any> {
                    return t.delay(2, TimeUnit.SECONDS)
                }
            })
            .take(3)
            .map { it.toString() }
            .subscribe(observer)
    }

    fun interval() {
        Observable.interval(1, TimeUnit.SECONDS)
            .take(5)
            .map { it.toString() }
            .subscribe(observer)
    }

    fun timer() {
        Observable.timer(1, TimeUnit.SECONDS)
            .map { it.toString() }
            .subscribe(observer)
    }
    
}