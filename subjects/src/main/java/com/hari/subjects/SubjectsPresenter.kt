package com.hari.subjects

import android.content.Context
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.*
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

    fun observableWithAutoConnect() {
        val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

        val autoConnectObservable = observable.publish().autoConnect()
        println("Wait for 3 seconds for observer1 to subscribe")
        Thread.sleep(3000)
        autoConnectObservable.subscribe(observer1)
        println("Wait for another 3 seconds for observer2 to subscribe")
        Thread.sleep(3000)
        autoConnectObservable.subscribe(observer2)
    }

    fun observableWithAutoConnectObservers() {
        val observable = Observable.range(1, 3)
            .subscribeOn(Schedulers.io())
            .map(object : Function<Int, Int> {
                override fun apply(input: Int): Int {
                    println("Inside map operator Squaring the input")
                    return input * input
                }
            })

        val autoConnectObservable = observable.publish().autoConnect(2)
        println("Wait for 3 seconds for observer1 to subscribe")
        Thread.sleep(3000)
        autoConnectObservable.subscribe(observer1)
        println("Wait for another 3 seconds for observer2 to subscribe")
        Thread.sleep(3000)
        autoConnectObservable.subscribe(observer2)
    }

    fun simplePublishSubject() {
        val publishSubject = PublishSubject.create<Int>()
        publishSubject.subscribe(observer1)
        publishSubject.onNext(1)
        publishSubject.onNext(2)
        publishSubject.onNext(3)
        publishSubject.subscribe(observer2)
        publishSubject.onNext(4)
        publishSubject.onNext(5)
        publishSubject.onComplete()
    }

    fun simpleBehaviorSubject() {
        val behaviorSubject = BehaviorSubject.create<Int>()
        behaviorSubject.subscribe(observer1)
        behaviorSubject.onNext(1)
        behaviorSubject.onNext(2)
        behaviorSubject.onNext(3)
        behaviorSubject.subscribe(observer2)
        behaviorSubject.onNext(4)
        behaviorSubject.onNext(5)
        behaviorSubject.onComplete()
    }

    fun simpleReplaySubject() {
        val replaySubject = ReplaySubject.create<Int>()
        replaySubject.subscribe(observer1)
        replaySubject.onNext(1)
        replaySubject.onNext(2)
        replaySubject.onNext(3)
        replaySubject.subscribe(observer2)
        replaySubject.onNext(4)
        replaySubject.onNext(5)
        replaySubject.onComplete()
    }

    fun simpleAsyncSubject() {
        val asyncSubject = AsyncSubject.create<Int>()
        asyncSubject.subscribe(observer1)
        asyncSubject.onNext(1)
        asyncSubject.onNext(2)
        asyncSubject.onNext(3)
        asyncSubject.subscribe(observer2)
        asyncSubject.onNext(4)
        asyncSubject.onNext(5)
        asyncSubject.onComplete()
    }

    fun simpleUnicastSubject() {
        val unicastSubject = UnicastSubject.create<Int>()
        unicastSubject.subscribe(observer1)
        unicastSubject.onNext(1)
        unicastSubject.onNext(2)
        unicastSubject.onNext(3)
        unicastSubject.subscribe(observer2)
        unicastSubject.onNext(4)
        unicastSubject.onNext(5)
        unicastSubject.onComplete()
    }

    fun simpleSingleSubject() {
        val single = Single.just(1)
        val singleSubject = SingleSubject.create<Int>()
        single.subscribe(singleSubject)
        singleSubject.subscribe(object: SingleObserver<Int>{
            override fun onSuccess(value: Int) {
                println("Single Subject Observer1 onSuccess $value")
            }

            override fun onSubscribe(d: Disposable) {
                println("Single Subject Observer1 onSubscribe")
            }

            override fun onError(e: Throwable) {
                println("Single Subject Observer1 onError $e")
            }
        })

        singleSubject.subscribe(object: SingleObserver<Int>{
            override fun onSuccess(value: Int) {
                println("Single Subject Observer2 onSuccess $value")
            }

            override fun onSubscribe(d: Disposable) {
                println("Single Subject Observer2 onSubscribe")
            }

            override fun onError(e: Throwable) {
                println("Single Subject Observer2 onError $e")
            }
        })
    }
}