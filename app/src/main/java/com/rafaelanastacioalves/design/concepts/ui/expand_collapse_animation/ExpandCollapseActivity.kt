package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation


import android.os.Bundle

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.domain.entities.FakeData
import com.rafaelanastacioalves.design.concepts.domain.entities.Resource


class ExpandCollapseActivity : AppCompatActivity(){

    private val mClickListener = this
    private var expandCollapseAdapter: ExpandCollapseAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private val mLiveDataMainEntityListViewModel: ExpandCollapseViewModel by lazy {
        ViewModelProvider(this).get(ExpandCollapseViewModel::class.java)
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
        setContentView(R.layout.expand_collapse_animation_activity)
    }

    private fun setupRecyclerView() {
        mRecyclerView = findViewById<View>(R.id.main_entity_list) as RecyclerView
        val layoutManager = LinearLayoutManager(applicationContext)
        mRecyclerView!!.layoutManager = layoutManager
        if (expandCollapseAdapter == null) {
            expandCollapseAdapter = ExpandCollapseAdapter(this)
        }
        mRecyclerView!!.adapter = expandCollapseAdapter
    }


    private fun populateRecyclerView(list: Resource<List<FakeData>>?) {
        if (list == null) {
            expandCollapseAdapter!!.setItems(null)
            //TODO add any error managing

        } else if (list.data!=null) {
            expandCollapseAdapter!!.setItems(list.data)
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
