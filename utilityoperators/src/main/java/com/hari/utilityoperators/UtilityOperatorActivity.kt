package com.hari.utilityoperators

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

/**
 * @author Hari Hara Sudhan.N
 */
class UtilityOperatorActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, UtilityOperatorActivity::class.java)
        }
    }

    private val presenter by lazy {
        UtilityOperatorPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_utility_operators)
    }

    override fun onClick(v: View?) {

    }
}