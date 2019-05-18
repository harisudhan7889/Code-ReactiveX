package com.hari.observables

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_observables.*

class ObservablesActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ObservablesActivity::class.java)
        }
    }

    private val presenter by lazy {
        ObservablesPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_observables)
        simpleObserverble.setOnClickListener(this)
        missingFlowable.setOnClickListener(this)
        dropStrategy.setOnClickListener(this)
        latestStrategy.setOnClickListener(this)
        errorStrategy.setOnClickListener(this)
        bufferStrategy.setOnClickListener(this)
        bufferWithCapacity.setOnClickListener(this)
        bufferWithOverFlow.setOnClickListener(this)
        bufferOverflowStrategy.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            simpleObserverble -> {
                presenter.simpleObservable()
            }
            missingFlowable -> {
                presenter.flowableWithMissingStrategy()
            }
            dropStrategy -> {
                presenter.flowableWithDropStrategy()
            }
            latestStrategy -> {
                presenter.flowableWithLatestStrategy()
            }
            errorStrategy -> {
                presenter.flowableWithErrorStrategy()
            }
            bufferStrategy -> {
                presenter.flowableWithUnboundedBufferStrategy()
            }
            bufferWithCapacity -> {
                presenter.flowableWithBoundedBufferStrategy()
            }
            bufferWithOverFlow -> {
                presenter.flowableWithBufferOverFlowAction()
            }
            bufferOverflowStrategy -> {
                presenter.flowableWithBufferOverFlowStrategy()
            }
        }
    }
}