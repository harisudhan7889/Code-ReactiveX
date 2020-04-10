package com.hari.subjects.behavioursubject.realtimesample1

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hari.subjects.R
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_pb_subject_one.*

/**
 * @author Hari Hara Sudhan.N
 */
class BehaviorSubjectActivityTwo: AppCompatActivity(), View.OnClickListener {

    private var disposable: Disposable?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pb_subject_one)
        supportActionBar?.title = "Behavior Subject View 2"
        addDetailsButton.setOnClickListener(this)
        disposable = RxBehviorSubjectBus.listen(String::class.java)
            .subscribe {
            details.visibility = View.VISIBLE
            details.text = it
        }
    }

    override fun onClick(v: View?) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable?.isDisposed != true) {
            disposable?.dispose()
        }
    }
}