package com.hari.subjects

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hari.subjects.behavioursubject.realtimesample1.BehaviorSubjectActivityOne
import com.hari.subjects.publishsubject.realtimesample1.RestaurantsActivity
import com.hari.subjects.publishsubject.realtimesample2.PublishSubjectActivityOne
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
        observableWithAutoConnect.setOnClickListener(this)
        autoConnectWithNoOfObservers.setOnClickListener(this)
        simplePublishSubject.setOnClickListener(this)
        publishSubject1.setOnClickListener(this)
        publishSubject2.setOnClickListener(this)
        simpleBehaviorSubject.setOnClickListener(this)
        behaviorSubject1.setOnClickListener(this)
        simpleReplaySubject.setOnClickListener(this)
        simpleAsyncSubject.setOnClickListener(this)
        simpleUnicastSubject.setOnClickListener(this)
        simpleSingleSubject.setOnClickListener(this)
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
            observableWithAutoConnect -> {
                presenter.observableWithAutoConnect()
            }
            autoConnectWithNoOfObservers -> {
                presenter.observableWithAutoConnectObservers()
            }
            simplePublishSubject -> {
                presenter.simplePublishSubject()
            }
            simpleBehaviorSubject -> {
                presenter.simpleBehaviorSubject()
            }
            simpleReplaySubject -> {
                presenter.simpleReplaySubject()
            }
            simpleAsyncSubject -> {
                presenter.simpleAsyncSubject()
            }
            simpleUnicastSubject -> {
                presenter.simpleUnicastSubject()
            }
            simpleSingleSubject -> {
                presenter.simpleSingleSubject()
            }
            publishSubject1 -> {
                startActivity(Intent(this, RestaurantsActivity::class.java))
            }
            publishSubject2 -> {
                startActivity(Intent(this, PublishSubjectActivityOne::class.java))
            }
            behaviorSubject1 -> {
                startActivity(Intent(this, BehaviorSubjectActivityOne::class.java))
            }
        }
    }
}