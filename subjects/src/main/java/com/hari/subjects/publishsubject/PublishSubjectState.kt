package com.hari.subjects.publishsubject

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * @author Hari Hara Sudhan.N
 */
open class PublishSubjectState<T> {
    private val loadRestaurantPublishSubject = PublishSubject.create<T>()

    fun observe(): Observable<T> {
        return loadRestaurantPublishSubject.distinctUntilChanged()
    }

    fun publish(value: T) {
        loadRestaurantPublishSubject.onNext(value)
    }
}