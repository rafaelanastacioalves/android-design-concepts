package com.example.rafaelanastacioalves.moby.ui.entitymainlisting


import android.content.Intent
import android.os.Build
import android.os.Bundle

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rafaelanastacioalves.moby.R
import com.example.rafaelanastacioalves.moby.domain.entities.FakeData
import com.example.rafaelanastacioalves.moby.domain.entities.MainEntity
import com.example.rafaelanastacioalves.moby.domain.entities.Resource

import timber.log.Timber

class MainActivity : AppCompatActivity(){

    private val mClickListener = this
    private var mainEntityAdapter: MainEntityAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private val mLiveDataMainEntityListViewModel: LiveDataMainEntityListViewModel by lazy {
        ViewModelProvider(this).get(LiveDataMainEntityListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        setupRecyclerView()
        title = "Expand/Collapse Animation"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        populateRecyclerView(generateFakeData())

    }

    private fun generateFakeData(): Resource<List<FakeData>>? {
        return Resource.success(arrayListOf(
                FakeData(1),
                FakeData(2),
                FakeData(3),
                FakeData(4),
                FakeData(5),
                FakeData(6),
                FakeData(7),
                FakeData(8),
                FakeData(9),
                FakeData(10),
                FakeData(11),
                FakeData(12),
                FakeData(13)
                ))
    }

    private fun setupViews() {
        setContentView(R.layout.main_entity_listing_activity)
        Timber.tag("LifeCycles")
        Timber.i("onCreate Activity")
    }

    private fun setupRecyclerView() {
        mRecyclerView = findViewById<View>(R.id.main_entity_list) as RecyclerView
        val layoutManager = LinearLayoutManager(applicationContext)
        mRecyclerView!!.layoutManager = layoutManager
        if (mainEntityAdapter == null) {
            mainEntityAdapter = MainEntityAdapter(this)
        }
        mRecyclerView!!.adapter = mainEntityAdapter
    }


    private fun populateRecyclerView(list: Resource<List<FakeData>>?) {
        if (list == null) {
            mainEntityAdapter!!.setItems(null)
            //TODO add any error managing
            Timber.w("Nothing returned from Main Entity List API")

        } else if (list.data!=null) {
            mainEntityAdapter!!.setItems(list.data)
        }

    }



//    private fun startActivityByVersion(mainEntity: MainEntity, transitionImageView: AppCompatImageView) {
//        val i = Intent(this, EntityDetailActivity::class.java)
//        i.putExtra(EntityDetailsFragment.ARG_ENTITY_ID, mainEntity.id)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            var bundle: Bundle? = null
//            bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity,
//                    transitionImageView, transitionImageView.transitionName).toBundle()
//            startActivity(i, bundle)
//
//        } else {
//            startActivity(i)
//        }
//    }
}
