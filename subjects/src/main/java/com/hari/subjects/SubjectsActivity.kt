package com.hari.subjects

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_subjects.*

/**
 * @author Hari Hara Sudhan.N
 */
class SubjectsActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SubjectsActivity::class.java)
        }
    }

    private val presenter by lazy {
        SubjectsPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_subjects)
        withoutSubject.setOnClickListener(this)
        withSubject.setOnClickListener(this)
        connectableObservable.setOnClickListener(this)
        observableWithRefCount.setOnClickListener(this)
        observableWithRefCountValue.setOnClickListener(this)
        observableWithRefCountTimer.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            withoutSubject -> {
                presenter.withoutSubject()
            }
            withSubject -> {
                presenter.withSubject()
            }
            connectableObservable -> {
                presenter.connectableObservable()
            }
            observableWithRefCount -> {
                presenter.observableWithRefCount()
            }
            observableWithRefCountValue -> {
                presenter.observableWithRefCountValue()
            }
            observableWithRefCountTimer -> {
                presenter.observableWithRefCountTimeout()
            }
        }
    }
}