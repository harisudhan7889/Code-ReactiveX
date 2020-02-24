package com.hari.conditionalandboolean

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_conditional_operators.*

/**
 * @author Hari Hara Sudhan.N
 */
class ConditionalOperatorActivity: AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ConditionalOperatorActivity::class.java)
        }
    }

    private val presenter by lazy {
        ConditionalOperatorPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_conditional_operators)
        all.setOnClickListener(this)
        amb.setOnClickListener(this)
        contains.setOnClickListener(this)
        defaultIfEmpty.setOnClickListener(this)
        sequenceEqual.setOnClickListener(this)
        skipUntil.setOnClickListener(this)
        takeUntil.setOnClickListener(this)
        skipWhile.setOnClickListener(this)
        takeWhile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            all -> {
                presenter.all()
            }
            amb -> {
                presenter.amb()
            }
            contains -> {
                presenter.contains()
            }
            defaultIfEmpty -> {
                presenter.defaultIfEmpty()
            }
            sequenceEqual -> {
                presenter.sequenceEqual()
            }
            skipUntil -> {
                presenter.skipUntil()
            }
            takeUntil -> {
                presenter.takeUntil()
            }
            skipWhile -> {
                presenter.skipWhile()
            }
            takeWhile -> {
                presenter.takeWhile()
            }
        }
    }
}