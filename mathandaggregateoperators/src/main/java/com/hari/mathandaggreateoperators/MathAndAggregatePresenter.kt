package com.hari.mathandaggreateoperators

import android.app.ProgressDialog
import android.content.Context
import com.hari.api.model.WinningCount
import com.hari.api.utils.AppUtils
import hu.akarnokd.rxjava2.math.MathObservable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiConsumer
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.lang.StringBuilder
import java.util.Comparator
import java.util.concurrent.Callable


/**
 * @author Hari Hara Sudhan.N
 */
class MathAndAggregatePresenter(private val context: Context) {

    fun averageDouble() {
        MathObservable.averageDouble(Observable.just(1, 2, 3))
                .subscribe(object : Observer<Double> {
                    override fun onComplete() {
                        System.out.println("averageDouble onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("averageDouble onSubscribe")
                    }

                    override fun onNext(average: Double) {
                        System.out.println("averageDouble Average Double Value is $average")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("averageDouble onError $error")
                    }
                })

    }

    fun averageFloat() {
        MathObservable.averageFloat(Observable.just(1, 2, 3))
                .subscribe(object : Observer<Float> {
                    override fun onComplete() {
                        System.out.println("averageFloat onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("averageFloat onSubscribe")
                    }

                    override fun onNext(average: Float) {
                        System.out.println("averageFloat Average Float Value is $average")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("averageFloat onError $error")
                    }
                })
    }

    fun max() {
        MathObservable.max(Observable.just(10, 23, 45, 5, 3))
                .subscribe(object : Observer<Int>{
                    override fun onComplete() {
                        System.out.println("max onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("max onSubscribe")
                    }

                    override fun onNext(maxValue: Int) {
                        System.out.println("max Maximmum Value is $maxValue")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("max onError $error")
                    }
                })
    }

    fun min() {
        MathObservable.min(Observable.just(10, 23, 45, 5, 3))
                .subscribe(object : Observer<Int>{
                    override fun onComplete() {
                        System.out.println("min onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("min onSubscribe")
                    }

                    override fun onNext(minValue: Int) {
                        System.out.println("min Minimmum Value is $minValue")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("min onError $error")
                    }
                })
    }

    fun sumDouble() {
        MathObservable.sumDouble(Observable.just(1.0, 2.0, 3.0))
                .subscribe(object : Observer<Double> {
                    override fun onComplete() {
                        System.out.println("sumDouble onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("sumDouble onSubscribe")
                    }

                    override fun onNext(sum: Double) {
                        System.out.println("sumDouble Sum of Double Value is $sum")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("sumDouble onError $error")
                    }
                })
    }

    fun sumFloat() {
        MathObservable.sumFloat(Observable.just(1F, 2F, 3F))
                .subscribe(object : Observer<Float> {
                    override fun onComplete() {
                        System.out.println("sumFloat onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("sumFloat onSubscribe")
                    }

                    override fun onNext(sum: Float) {
                        System.out.println("sumFloat Sum of Float Value is $sum")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("sumFloat onError $error")
                    }
                })
    }

    fun sumInt() {
        MathObservable.sumInt(Observable.just(1, 2, 3))
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        System.out.println("sumInt onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("sumInt onSubscribe")
                    }

                    override fun onNext(sum: Int) {
                        System.out.println("sumInt Sum of Integer Value is $sum")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("sumInt onError $error")
                    }
                })
    }

    fun sumLong() {
        MathObservable.sumLong(Observable.just(100098989898, 27867878787, 2323344545))
                .subscribe(object : Observer<Long> {
                    override fun onComplete() {
                        System.out.println("sumLong onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("sumLong onSubscribe")
                    }

                    override fun onNext(sum: Long) {
                        System.out.println("sumLong Sum of Long Value is $sum")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("sumLong onError $error")
                    }
                })
    }

    fun count() {
        Observable.just(2, 3, 5, 6, 7)
                .count()
                .subscribe(object : SingleObserver<Long> {
                    override fun onSuccess(noOfItems: Long) {
                        System.out.println("count no of items emitted $noOfItems")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("count onSubscribe")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("count onError $error")
                    }
                })
    }

    fun reduceWith1() {
        Observable.just(1,2,3,4,5)
                .reduceWith(object : Callable<Int>{
                    override fun call(): Int {
                        return 9
                    }
                }, object : BiFunction<Int, Int, Int> {
                    override fun apply(previousResult: Int, currentValue: Int): Int {
                        System.out.println("reduceWith previous value $previousResult and current value $currentValue")
                        return previousResult + currentValue
                    }
                }).subscribe(object : SingleObserver<Int> {
                    override fun onSuccess(totalValue: Int) {
                        System.out.println("reduceWith total value $totalValue")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("reduceWith onSubscribe")
                    }

                    override fun onError(error: Throwable) {
                        System.out.println("reduceWith onError $error")
                    }
                })
    }

    fun reduceWith2() {
        val progressBar = ProgressDialog(context)
        val countries = AppUtils.getWorldCupWinners()
        Observable.fromIterable(countries)
                .reduceWith(object : Callable<WinningCount> {
                    override fun call(): WinningCount {
                        return getInitialWCWinner()
                    }
                }, object : BiFunction<WinningCount, WinningCount, WinningCount> {
                    override fun apply(accumulator: WinningCount, newObject: WinningCount): WinningCount {
                        if (accumulator.winningCounts.containsKey(newObject.countryName)) {
                            val winningCount = accumulator.winningCounts[newObject.countryName] ?: 0
                            accumulator.winningCounts[newObject.countryName] = winningCount.inc()
                        } else {
                            accumulator.winningCounts[newObject.countryName] = 1
                        }
                        return accumulator
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<WinningCount> {
                    override fun onSuccess(accumulator: WinningCount) {
                        progressBar.dismiss()
                        accumulator.winningCounts.forEach {
                            System.out.println("reduceWith onSuccess: No of times ${it.key} had won the Cricket World Cup is ${it.value}")
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        progressBar.show()
                        System.out.println("reduceWith onSubscribe")
                    }

                    override fun onError(e: Throwable) {
                        progressBar.dismiss()
                        System.out.println("reduceWith onError $e")
                    }

                })
    }

    fun toList() {
        Observable.just(1,2 ,3, 4, 5)
                .toList()
                .subscribe(object : SingleObserver<List<Int>>{
                    override fun onSuccess(list: List<Int>) {
                        System.out.println("toList onSuccess ${list.size}")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("toList onSubscribe")
                    }

                    override fun onError(e: Throwable) {
                        System.out.println("toList onError $e")
                    }
                })
    }

    fun toSortedList() {
        Observable.just(1,5 ,3, 2, 6)
                .toSortedList(object : Comparator<Int>{
                    override fun compare(nextItem: Int, currentItem: Int): Int {
                        return Math.max(currentItem, nextItem)
                    }
                }).subscribe(object : SingleObserver<List<Int>>{
                    override fun onSuccess(sortedList: List<Int>) {
                        System.out.println("toSortedList onSuccess $sortedList")
                    }

                    override fun onSubscribe(d: Disposable) {
                        System.out.println("toSortedList onSubscribe")
                    }

                    override fun onError(e: Throwable) {
                        System.out.println("toSortedList onError $e")
                    }
                })
    }

    private fun getInitialWCWinner(): WinningCount {
        Thread.sleep(2000)
        return WinningCount("")
    }

}