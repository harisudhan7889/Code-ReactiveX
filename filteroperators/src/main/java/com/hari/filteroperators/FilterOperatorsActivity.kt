package com.hari.filteroperators

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_filter_operators.*

/**
 * @author Hari Hara Sudhan.N
 */
class FilterOperatorsActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, FilterOperatorsActivity::class.java)
        }
    }

    private val presenter by lazy {
        FilterOperatorPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_filter_operators)
        distinct.setOnClickListener(this)
        elementAt.setOnClickListener(this)
        elementAtOrError.setOnClickListener(this)
        elementAtWithDefaultValue.setOnClickListener(this)
        filter.setOnClickListener(this)
        ignoreElements.setOnClickListener(this)
        skip.setOnClickListener(this)
        skipLast.setOnClickListener(this)
        take.setOnClickListener(this)
        takeLast.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            distinct -> {
                presenter.distinctOperator()
            }
            elementAt -> {
                presenter.elementAtOperator()
            }
            elementAtOrError -> {
                presenter.elementAtOrErrorOperator()
            }
            elementAtWithDefaultValue -> {
                presenter.elementAtWithDefaultValue()
            }
            filter -> {
                presenter.filter()
            }
            ignoreElements -> {
                presenter.ignoreElements()
            }
            skip -> {
                presenter.skip()
            }
            skipLast -> {
                presenter.skipLast()
            }
            take -> {
                presenter.take()
            }
            takeLast -> {
                presenter.takeLast()
            }

        }
    }
}