package com.hari.combineoperator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_combine_operators.*

class CombineOperatorsActivity : AppCompatActivity(),
        View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, CombineOperatorsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_combine_operators)
        startWith.setOnClickListener(this)
        merge.setOnClickListener(this)
        mergeDelayError.setOnClickListener(this)
    }

    private val presenter by lazy {
        CombineOperatorsPresenter(this)
    }

    override fun onClick(v: View?) {
      when(v) {
          startWith -> {
              presenter.startWith()
          }
          merge -> {
              presenter.mergeWith()
          }
          mergeDelayError -> {
              presenter.mergeDelayError()
          }
      }
    }
}