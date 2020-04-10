package com.hari.subjects.publishsubject.realtimesample2

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * @author Hari Hara Sudhan.N
 */
object RxPublishSubjectBus {

    private val publisher = PublishSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    fun <T> observe(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)

}