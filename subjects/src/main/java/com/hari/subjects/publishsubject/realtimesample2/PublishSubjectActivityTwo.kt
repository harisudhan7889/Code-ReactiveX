package com.hari.subjects.publishsubject.realtimesample2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hari.subjects.R
import kotlinx.android.synthetic.main.view_pb_subject_two.*

/**
 * @author Hari Hara Sudhan.N
 */
class PublishSubjectActivityTwo: AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pb_subject_two)
        supportActionBar?.title = "Publish Subject View 2"
        done.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        RxPublishSubjectBus.publish(detailTextEditor.text.toString())
        finish()
    }
}