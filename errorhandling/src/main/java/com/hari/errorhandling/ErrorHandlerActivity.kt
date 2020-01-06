package com.hari.errorhandling

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_error_handler.*

/**
 * @author Hari Hara Sudhan.N
 */
class ErrorHandlerActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ErrorHandlerActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_error_handler)
        exceptionResumeNext.setOnClickListener(this)
        errorResumeNext.setOnClickListener(this)
        exceptionResumeNextInMap.setOnClickListener(this)
        errorReturn.setOnClickListener(this)
        errorReturnItem.setOnClickListener(this)
        retryWhen.setOnClickListener(this)
    }

    private val presenter by lazy {
        ErrorHandlerPresenter(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            exceptionResumeNext -> {
                presenter.onExceptionResumeNext()
            }
            errorResumeNext -> {
                presenter.onErrorResumeNext()
            }
            exceptionResumeNextInMap -> {
                presenter.onErrorResumeNextInsideMap()
            }
            errorReturn -> {
                presenter.onErrorReturn()
            }
            errorReturnItem -> {
                presenter.onErrorReturnItem()
            }
            retryWhen -> {
                presenter.retryWhen()
            }
        }
    }
}