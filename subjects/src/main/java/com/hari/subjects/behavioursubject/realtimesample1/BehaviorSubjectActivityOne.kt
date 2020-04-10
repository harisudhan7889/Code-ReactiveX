package com.hari.subjects.behavioursubject.realtimesample1

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hari.subjects.R
import kotlinx.android.synthetic.main.view_pb_subject_two.*

/**
 * @author Hari Hara Sudhan.N
 */
class BehaviorSubjectActivityOne: AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pb_subject_two)
        supportActionBar?.title = "Behavior Subject View 1"
        done.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        RxBehviorSubjectBus.publish(detailTextEditor.text.toString())
        startActivity(Intent(this, BehaviorSubjectActivityTwo::class.java))
    }
}