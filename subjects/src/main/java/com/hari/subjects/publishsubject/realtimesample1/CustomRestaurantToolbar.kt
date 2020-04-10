package com.hari.subjects.publishsubject.realtimesample1

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import com.hari.subjects.SubjectApplication
import io.reactivex.disposables.Disposable

/**
 * @author Hari Hara Sudhan.N
 */
class CustomRestaurantToolbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Toolbar(context, attrs, defStyleAttr) {

    private var disposable: Disposable? = null

    init {
        disposable = SubjectApplication.getSubjectApplication()?.retrieveLoadRestaurantState()?.observe()?.subscribe {
            title = "Restaurant Count ${it.restaurants.count() - 1}"
        }
    }
}