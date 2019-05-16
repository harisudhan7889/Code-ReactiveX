package com.hari.observables

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

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

}