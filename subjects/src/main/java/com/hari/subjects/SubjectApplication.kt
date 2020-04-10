package com.hari.subjects

import android.app.Application
import com.hari.subjects.publishsubject.realtimesample1.LoadRestaurantState

/**
 * @author Hari Hara Sudhan.N
 */
class SubjectApplication: Application() {

    companion object {
        private var application: SubjectApplication? = null
        fun getSubjectApplication(): SubjectApplication? {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }

    private val loadRestaurantState by lazy {
        LoadRestaurantState()
    }

    fun retrieveLoadRestaurantState(): LoadRestaurantState {
        return loadRestaurantState
    }
}