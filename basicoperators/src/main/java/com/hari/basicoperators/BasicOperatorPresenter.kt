package com.hari.rxjava.operators.basic

/**
 * @author Hari Hara Sudhan.N
 */
interface BasicOperatorPresenter {
    interface View {
        fun onSubscribe()
        fun onComplete()
        fun onNext(value: String)
        fun onError(error: Throwable)
    }
}