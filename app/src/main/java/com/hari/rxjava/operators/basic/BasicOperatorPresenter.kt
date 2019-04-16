package com.hari.rxjava.operators.basic

interface BasicOperatorPresenter {
    interface View {
        fun onSubscribe()
        fun onComplete()
        fun onNext(value: String)
        fun onError(error: Throwable)
    }
}