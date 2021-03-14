package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.custom.filterlayout.FilterLayoutContract
import com.rafaelanastacioalves.design.concepts.domain.entities.FakeData
import com.rafaelanastacioalves.design.concepts.domain.entities.Resource
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.*


class ExpandCollapseActivity : AppCompatActivity(), FilterLayoutContract {


    internal val expandCollapseAdapter: ExpandCollapseAdapter by lazy {
        ExpandCollapseAdapter(this)
    }
    private val animationDelegate by lazy {
        ExpandCollapseActivityDelegate(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        // TODO: Refactor - deixar mais explicita a mudança de tela motion e não motion (07/02/2021)
        filterLayoutMotion.isVisible = false
        filterLayoutNormal.isVisible = true

        setupExpandCollapseRecyclerView()
        title = getString(R.string.expand_collapse_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        populateRecyclerView(generateFakeData())
        setupFab()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupFilterLayout()
    }

    private fun setupFilterLayout() {
        filterLayout.setFilterLayoutCallbacksListeners(this)
//        filterLayout.calculateTabDimensions()
    }

    private fun setupFab() {
        fab.setOnClickListener {
            animationDelegate.animateFilterShowUp(true)
        }
    }

    internal fun hideFab() {
        fab.isVisible = false
    }

    internal fun showFab() {
        fab.isVisible = true
    }

    internal fun showFilter() {
        filterLayout.isVisible = true
    }

    internal fun hideFilter() {
        filterLayout.isVisible = false
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

    private fun setupExpandCollapseRecyclerView() {
        val layoutManager = LinearLayoutManager(applicationContext)
        mainEntityList!!.layoutManager = layoutManager
        mainEntityList!!.adapter = expandCollapseAdapter
    }

    private fun populateRecyclerView(list: Resource<List<FakeData>>?) {
        if (list == null) {
            expandCollapseAdapter.setItems(null)
        } else if (list.data != null) {
            expandCollapseAdapter.setItems(list.data)
        }
    }

    override fun onFilterDismiss() {
        animationDelegate.animateFilterShowUp(false)
    }

    override fun onFilterConfirmed() {
        animationDelegate.animateFilterShowUp(false)
    }
}
