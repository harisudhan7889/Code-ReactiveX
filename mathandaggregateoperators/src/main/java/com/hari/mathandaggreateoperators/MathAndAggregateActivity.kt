package com.hari.mathandaggreateoperators

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_math_operators.*

/**
 * @author Hari Hara Sudhan.N
 */
class MathAndAggregateActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MathAndAggregateActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_math_operators)
        averageDouble.setOnClickListener(this)
        averageFloat.setOnClickListener(this)
        max.setOnClickListener(this)
        min.setOnClickListener(this)
        sumDouble.setOnClickListener(this)
        sumFloat.setOnClickListener(this)
        sumInt.setOnClickListener(this)
        sumLong.setOnClickListener(this)
        count.setOnClickListener(this)
        reduceWith.setOnClickListener(this)
        reduceWith2.setOnClickListener(this)
        toList.setOnClickListener(this)
        toSortedList.setOnClickListener(this)
        toListCountries.setOnClickListener(this)
        toSortedCountriesList.setOnClickListener(this)
    }

    private val presenter by lazy {
        MathAndAggregatePresenter(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            averageDouble -> {
                presenter.averageDouble()
            }
            averageFloat -> {
                presenter.averageFloat()
            }
            max->{
                presenter.max()
            }
            min->{
                presenter.min()
            }
            sumDouble -> {
                presenter.sumDouble()
            }
            sumFloat -> {
                presenter.sumFloat()
            }
            sumInt -> {
                presenter.sumInt()
            }
            sumLong -> {
                presenter.sumLong()
            }
            count -> {
                presenter.count()
            }
            reduceWith -> {
                presenter.reduceWith1()
            }
            reduceWith2 -> {
                presenter.reduceWith2()
            }
            toList -> {
                presenter.toList()
            }
            toSortedList -> {
                presenter.toSortedList()
            }
            toListCountries -> {
                presenter.toListOfCountries()
            }
            toSortedCountriesList -> {
                presenter.toSortedCountriesList()
            }
        }
    }
}