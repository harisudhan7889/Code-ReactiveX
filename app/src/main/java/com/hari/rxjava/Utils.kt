package com.hari.rxjava

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager

/**
 * @author Hari Hara Sudhan.N
 */
object Utils {
    fun hideKeyboard(activity: AppCompatActivity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = activity.currentFocus
        focusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }
}