package com.hari.subjects.behavioursubject.realtimesample1

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * @author Hari Hara Sudhan.N
 */
object RxBehviorSubjectBus {
    private val publisher = BehaviorSubject.create<Any>()

    fun publish(event: Any) {
        publisher.onNext(event)
    }

    fun <T> listen(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}