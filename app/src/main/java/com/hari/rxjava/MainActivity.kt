package com.hari.rxjava

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.hari.basicoperators.BasicOperatorsActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Hari Hara Sudhan.N
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        basicOperators.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            basicOperators -> {
                startActivity(BasicOperatorsActivity.getIntent(this))
            }
        }
    }
}
