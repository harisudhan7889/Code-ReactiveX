package com.hari.observables

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_observables.*

/**
 * @author Hari Hara Sudhan.N
 */
class ObservablesActivity : AppCompatActivity(), View.OnClickListener {

    private var latitude = 0.0
    private var longitude = 0.0

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, ObservablesActivity::class.java)
        }
    }

    private val presenter by lazy {
        ObservablesPresenter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_observables)
        getGeoLocations()
        simpleObserverble.setOnClickListener(this)
        missingFlowable.setOnClickListener(this)
        dropStrategy.setOnClickListener(this)
        latestStrategy.setOnClickListener(this)
        errorStrategy.setOnClickListener(this)
        bufferStrategy.setOnClickListener(this)
        bufferWithCapacity.setOnClickListener(this)
        bufferWithOverFlow.setOnClickListener(this)
        bufferOverflowStrategy.setOnClickListener(this)
        single.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            simpleObserverble -> {
                presenter.simpleObservable()
            }
            missingFlowable -> {
                presenter.flowableWithMissingStrategy()
            }
            dropStrategy -> {
                presenter.flowableWithDropStrategy()
            }
            latestStrategy -> {
                presenter.flowableWithLatestStrategy()
            }
            errorStrategy -> {
                presenter.flowableWithErrorStrategy()
            }
            bufferStrategy -> {
                presenter.flowableWithUnboundedBufferStrategy()
            }
            bufferWithCapacity -> {
                presenter.flowableWithBoundedBufferStrategy()
            }
            bufferWithOverFlow -> {
                presenter.flowableWithBufferOverFlowAction()
            }
            bufferOverflowStrategy -> {
                presenter.flowableWithBufferOverFlowStrategy()
            }
            single->{
                presenter.singleObservable(latitude, longitude)
            }
        }
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
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?:locationManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER)
            location?.let {
                latitude = location.latitude
                longitude = location.longitude
            }
        }

    }
}