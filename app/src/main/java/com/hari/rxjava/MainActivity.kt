package com.hari.rxjava

import android.os.Bundle
import android.provider.ContactsContract
import android.provider.UserDictionary
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hari.basicoperators.BasicOperatorsActivity
import com.hari.combineoperator.CombineOperatorsActivity
import com.hari.conditionalandboolean.ConditionalOperatorActivity
import com.hari.errorhandling.ErrorHandlerActivity
import com.hari.filteroperators.FilterOperatorsActivity
import com.hari.mathandaggreateoperators.MathAndAggregateActivity
import com.hari.observables.ObservablesActivity
import com.hari.subjects.SubjectsActivity
import com.hari.transformoperators.TransformOperatorsActivity
import com.hari.utilityoperators.UtilityOperatorActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Hari Hara Sudhan.N
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        basicOperators.setOnClickListener(this)
        transformOperators.setOnClickListener(this)
        observableType.setOnClickListener(this)
        combineOperator.setOnClickListener(this)
        mathOperator.setOnClickListener(this)
        filterOperator.setOnClickListener(this)
        conditionalOperator.setOnClickListener(this)
        utilityOperator.setOnClickListener(this)
        errorHandler.setOnClickListener(this)
        subject.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            basicOperators -> {
                startActivity(BasicOperatorsActivity.getIntent(this))
            }
            transformOperators -> {
                startActivity(TransformOperatorsActivity.getIntent(this))
            }
            observableType -> {
                startActivity(ObservablesActivity.getIntent(this))
            }
            combineOperator -> {
                startActivity(CombineOperatorsActivity.getIntent(this))
            }
            mathOperator -> {
                startActivity(MathAndAggregateActivity.getIntent(this))
            }
            filterOperator -> {
                startActivity(FilterOperatorsActivity.getIntent(this))
            }
            conditionalOperator -> {
                startActivity(ConditionalOperatorActivity.getIntent(this))
            }
            utilityOperator -> {
                startActivity(UtilityOperatorActivity.getIntent(this))
            }
            errorHandler -> {
                startActivity(ErrorHandlerActivity.getIntent(this))
            }
            subject -> {
                startActivity(SubjectsActivity.getIntent(this))
            }
        }
    }

}
