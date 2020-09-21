package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation


import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.custom.filterlayout.FilterLayoutContract
import com.rafaelanastacioalves.design.concepts.domain.entities.FakeData
import com.rafaelanastacioalves.design.concepts.domain.entities.Resource
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.*
import kotlin.math.roundToInt


class ExpandCollapseActivity : AppCompatActivity(), FilterLayoutContract {

    private var tabMaxHeight: Int = 0
    private val mClickListener = this
    private var expandCollapseAdapter: ExpandCollapseAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    var filterMaxWith: Int = 0
    var filterMaxHeight: Int = 0

    var fabMiddlePosition: Float = 0f

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

        setupFab()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupFilterLayout()
    }

    private fun setupFilterLayout() {
        filterLayout.init(this)
//        filterLayout.calculateTabDimensions()
    }

    private fun setupFab() {
        fab.setOnClickListener {
            val constraintSet = ConstraintSet()
            constraintSet.clone(constraint)
            constraintSet.clear(fab.id)
            constraintSet.applyTo(constraint)
            animate()


        }
    }

    private fun animate() {
        val animateFab = animateFab()
        val animateShowFilter = animateShowFilter()
        val animateFilterExpansion = filterLayout.animateExpansion()

        val animatorSet = AnimatorSet()
        animatorSet.play(animateFab).before(animateShowFilter)
        animatorSet.play(animateShowFilter).before(animateFilterExpansion)
        animatorSet.start()
    }

    private fun animateFab(): ValueAnimator {
        val valueAnimator = ExpandCollapseAnimationDelegate.getValueAnimator(true, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            fab.translationX = -400f * progress
            fab.translationY = -400f * progress
        }

        valueAnimator.doOnEnd {
            calculateFabPosition()
        }
        return valueAnimator
    }

    private fun calculateFabPosition() {
        fabMiddlePosition = fab.y
    }

    private fun animateShowFilter(): ValueAnimator {
        val valueAnimator = ExpandCollapseAnimationDelegate.getValueAnimator(true, 1500, AccelerateDecelerateInterpolator()) { progress ->

            filterLayout.alpha = progress
            filterLayout.layoutParams.width = (progress * filterMaxWith).roundToInt()
            println("Width: ${filterLayout.layoutParams.width}")

            fab.alpha = 1 - progress
            fab.y = fabMiddlePosition + (this.screenHeight - fabMiddlePosition - 400f) * (progress)
            filterLayout.layoutParams.height = (progress * filterLayout.withoutTabsHeight.toFloat()).toInt()
            println("Height: ${filterLayout.layoutParams.height}")

            filterLayout.requestLayout()
        }
        valueAnimator.doOnStart {
            filterLayout.preparetoOpenAnimation()
            filterLayout.doOnPreDraw {
                if ((filterMaxWith + filterMaxHeight) == 0) {
                    calculateFilterFinalDimensions(it)
                    filterLayout.alpha = 0f

                }
            }
            showFilter()
        }
        valueAnimator.doOnEnd {
            hideFab()
        }
        return valueAnimator
    }

    private fun calculateFilterFinalDimensions(view: View) {
        if (filterMaxHeight == 0 || filterMaxWith == 0) {
            filterMaxWith = view.measuredWidth
            filterMaxHeight = view.height
        }
    }

    private fun hideFab() {
        fab.isVisible = false
    }

    private fun showFab() {
        fab.isVisible = true
    }

    private fun showFilter() {
        filterLayout.isVisible = true
    }

    private fun hideFilter() {
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

    override fun onFilterDismiss() {
        hideFilter()
        showFab()
    }

    override fun onFilterConfirmed() {
        hideFilter()
        showFab()
    }
}
