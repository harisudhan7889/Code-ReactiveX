package com.hari.combineoperator

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.view_combine_operators.*
import java.util.concurrent.TimeUnit

/**
 * @author Hari Hara Sudhan.N
 */
class CombineOperatorsActivity : AppCompatActivity(),
        View.OnClickListener {

    private var latitude = 0.0
    private var longitude = 0.0

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, CombineOperatorsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_combine_operators)
        getGeoLocations()
        startWith.setOnClickListener(this)
        merge.setOnClickListener(this)
        mergeDelayError.setOnClickListener(this)
        zip.setOnClickListener(this)
        initRxBindingsForCombineLatest()
    }

    private fun initRxBindingsForCombineLatest() {
        val userNameObservable = RxTextView.textChanges(userName).debounce(300, TimeUnit.MILLISECONDS)
        val passwordObservable = RxTextView.textChanges(password).debounce(300, TimeUnit.MILLISECONDS)
        val signUpButtonConsumer = RxView.enabled(signUpButton)
        val errorConsumer = object : Consumer<Throwable> {
            override fun accept(error: Throwable?) {
                System.out.println("onError $error")
            }
        }
        presenter.combineLatest(userNameObservable, passwordObservable, signUpButtonConsumer, errorConsumer)
    }

    private fun getGeoLocations() {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnabled
                && ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?:locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            location?.let {
                latitude = location.latitude
                longitude = location.longitude
            }
        }
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
          zip -> {
              presenter.zip(latitude, longitude)
          }
      }
    }
}