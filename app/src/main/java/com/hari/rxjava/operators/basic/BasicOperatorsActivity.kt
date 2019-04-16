package com.hari.rxjava.operators.basic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hari.rxjava.R
import com.hari.rxjava.Utils
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_basic_operators.*

/**
 * @author Hari Hara Sudhan.N
 */
class BasicOperatorsActivity : AppCompatActivity(),
    View.OnClickListener {

    private val observer = object : Observer<String?> {

        override fun onComplete() {
            output.text = output.text.toString().plus("\n").plus("onComplete")
            Utils.hideKeyboard(this@BasicOperatorsActivity)
        }

        override fun onSubscribe(d: Disposable) {
            output.text = output.text.toString().plus("\n").plus("onSubscribe")
        }

        override fun onNext(value: String) {
            output.text = output.text.toString().plus("\n").plus(value)
        }

        override fun onError(error: Throwable) {
            output.text = output.text.toString().plus("\n").plus("onError "+error.message)
        }
    }

    private val presenter by lazy {
        BasicOperatorsPresenterImpl(observer)
    }

    companion object {
     fun getIntent(context: Context): Intent {
         return Intent(context, BasicOperatorsActivity::class.java)
     }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_basic_operators)
        createButton.setOnClickListener(this)
        justButton.setOnClickListener(this)
        deferButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            createButton -> {
                output.text = ""
                presenter.create(createTextBox.text.toString())
            }
            justButton -> {
                output.text = ""
                presenter.just(createTextBox.text.toString())
            }
            deferButton -> {
                output.text = ""
                presenter.defer()
            }
        }
    }
}