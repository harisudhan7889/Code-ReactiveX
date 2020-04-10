package com.hari.subjects.publishsubject.realtimesample1

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.hari.api.model.Restaurants
import com.hari.subjects.R
import com.hari.subjects.SubjectApplication
import com.hari.subjects.common.Listener
import com.hari.subjects.common.RestaurantAdapter
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.view_publish_subject.*

/**
 * @author Hari Hara Sudhan.N
 */
class RestaurantsActivity : AppCompatActivity(), Listener {

    private var adapter: RestaurantAdapter? = null
    private var disposable: Disposable? = null

    private val progressBar by lazy {
        ProgressDialog(this)
    }

    private val presenter by lazy {
        RestaurantsPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_publish_subject)
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        adapter = RestaurantAdapter(this)
        recyclerView.adapter = adapter
        refreshContainer.setOnRefreshListener {
            loadRestaurantForFirstTime()
        }
        bind()
    }

    override fun onResume() {
        super.onResume()
        loadRestaurantForFirstTime()
    }

    override fun onMoreItemClick() {
        progressBar.show()
        presenter.loadMoreRestaurants()
    }

    override fun onPause() {
        super.onPause()
        unbind()
    }

    private fun loadRestaurantForFirstTime() {
        presenter.loadRestaurantsForFirsTime("9.925201", "78.119774")
    }

    private fun bind() {
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        disposable = (application as SubjectApplication).retrieveLoadRestaurantState().observe().subscribe {
            updateUIWithRestaurants(it)
            progressBar.dismiss()
            refreshContainer.isRefreshing = false
        }
    }

    private fun updateUIWithRestaurants(restaurants: Restaurants?) {
        adapter?.setAdapterData(restaurants?.restaurants)
        adapter?.notifyDataSetChanged()
    }

    private fun unbind() {
        if (disposable?.isDisposed == false) {
            disposable?.dispose()
        }
    }
}