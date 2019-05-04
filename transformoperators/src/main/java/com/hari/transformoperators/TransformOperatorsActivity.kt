package com.hari.transformoperators

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.view_transform_operators.*

/**
 * @author Hari Hara Sudhan.N
 */
class TransformOperatorsActivity : AppCompatActivity(),
    View.OnClickListener {

    private var latitude = 0.0
    private var longitude = 0.0

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, TransformOperatorsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_transform_operators)
        getGeoLocations()
        buffer.setOnClickListener(this)
        map.setOnClickListener(this)
        flatMap.setOnClickListener(this)
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
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            location.let {
                latitude = location.latitude
                longitude = location.longitude
            }
        }

    }

    private val presenter by lazy {
        TransformOperatorsPresenter(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            buffer -> {
                presenter.buffer()
            }
            map -> {
                presenter.map()
            }
            flatMap -> {
                presenter.flatMap(latitude, longitude)
            }
        }
    }
}