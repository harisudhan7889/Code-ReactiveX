package com.hari.subjects

import android.content.Context
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit

/**
 * @author Hari Hara Sudhan.N
 */
class SubjectsPresenter(private val context: Context) {

    private val observer1 = object : Observer<Int> {
        override fun onComplete() {
            println("Observer1 onComplete")
        }

        override fun onSubscribe(d: Disposable) {
            println("Observer1 onSubscribe")
        }

        override fun onNext(output: Int) {
            println("Observer1 Output $output")
        }

        override fun onError(e: Throwable) {
            println("Observer1 onError")
        }
    }

    private val observer2 = object: Observer<Int> {
        override fun onComplete() {
            println("Observer2 onComplete")
        }

        override fun onSubscribe(d: Disposable) {
            println("Observer2 onSubscribe")
        }

        override fun onNext(output: Int) {
            println("Observer2 Output $output")
        }

        override fun onError(e: Throwable) {
            println("Observer2 onError")
        }
    }

    private val observer3 = object : DisposableObserver<Long>() {
        override fun onComplete() {
            println("Observer3 onComplete")
        }

        override fun onNext(output: Long) {
            println("Observer3 Output $output")
        }

        override fun onError(e: Throwable) {
            println("Observer3 onError")
        }
    }

    private val observer4 = object : DisposableObserver<Long>() {
        override fun onComplete() {
            println("Observer4 onComplete")
        }

        override fun onNext(output: Long) {
            println("Observer4 Output $output")
        }

        override fun onError(e: Throwable) {
            println("Observer4 onError")
        }
    }

    fun withoutSubject() {
        val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })
        observable.subscribe(observer1)
        observable.subscribe(observer2)
    }

    fun withSubject() {
        val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

        // Creating a subject and subscribing it with observable
        val subject = ReplaySubject.create<Int>()
        observable.subscribe(subject)

        // now subscribing or adding observers to subject.
        subject.subscribeWith(observer1)
        subject.subscribe(observer2)
    }

    fun connectableObservable() {
        //Part1 - Creating cold observable from scratch
        val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

        //Part2 - Converting cold observable to hot observable
        val connectableObservable = observable.publish()

        //Part3 - Subscribing observers to the ConnectableObservable
        connectableObservable.subscribe(observer1)
        connectableObservable.subscribe(observer2)

        //Part4 - Call connect method of ConnectableObservable
        connectableObservable.connect()
    }

    fun observableWithRefCount() {
        val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

        val refCountObservable = observable.publish().refCount()
        println("Wait for 3 seconds for observer1 and observer2 to subscribe")
        Thread.sleep(3000)
        refCountObservable.subscribe(observer1)
        refCountObservable.subscribe(observer2)
    }

    fun observableWithRefCountValue() {
        val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

        val refCountObservable = observable.publish().refCount(2)
        println("Wait for 3 seconds for observer1 to subscribe")
        Thread.sleep(3000)
        refCountObservable.subscribe(observer1)
        println("Wait for another 3 seconds for observer2 to subscribe")
        Thread.sleep(3000)
        refCountObservable.subscribe(observer2)
    }

    fun observableWithRefCountTimeout() {
        val observable = Observable.interval(2000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Long, Long> {
                override fun apply(input: Long): Long {
                    println("Inside map operator returns the same input.")
                    return input
                }
            })

        val refCountObservable = observable.publish().refCount(12000, TimeUnit.MILLISECONDS)
        refCountObservable.subscribe(observer3)
        Thread.sleep(4000)
        refCountObservable.subscribe(observer4)
        Thread.sleep(3000)
        observer3.dispose()
        observer4.dispose()
    }
}