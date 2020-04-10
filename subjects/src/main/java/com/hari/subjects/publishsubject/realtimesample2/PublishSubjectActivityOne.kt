package com.hari.subjects.publishsubject.realtimesample2

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hari.subjects.R
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_pb_subject_one.*

/**
 * @author Hari Hara Sudhan.N
 */
class PublishSubjectActivityOne: AppCompatActivity(), View.OnClickListener {

    private var disposable: Disposable?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pb_subject_one)
        addDetailsButton.setOnClickListener(this)
        supportActionBar?.title = "Publish Subject View 1"
        disposable = RxPublishSubjectBus.observe(String::class.java).subscribe {
            details.visibility = View.VISIBLE
            details.text = it
        }
    }

    override fun onClick(v: View?) {
        startActivity(Intent(this, PublishSubjectActivityTwo::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable?.isDisposed != true) {
            disposable?.dispose()
        }
    }
}