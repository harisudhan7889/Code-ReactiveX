package com.hari.observables

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_observables.*

class ObservablesActivity : AppCompatActivity(), View.OnClickListener {

    private val presenter by lazy {
        ObservablesPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_observables)
        simpleObserverble.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            simpleObserverble -> {
                presenter.simpleObservable()
            }
        }
    }
}