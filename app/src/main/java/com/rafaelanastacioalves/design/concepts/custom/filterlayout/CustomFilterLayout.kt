package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.domain.entities.CustomFilterLayoutTabItemElement
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation.ExpandCollapseAnimationDelegate
import kotlinx.android.synthetic.main.custom_filterlayout.view.*
import kotlin.math.roundToInt

class FilterLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ViewPagerItemsContract {

    private val tabMaxHeight by lazy { resources.getDimension(R.dimen.customLayoutTabMaxHeight) }
    var withoutTabsHeight: Int = 0
    private lateinit var delegate: FilterLayoutContract
    val list = generateTabItemViewHolderData()
    lateinit var tabAdapterForViewPager: TabForViewPagerAdapter
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    var totalDxTabScroll: Int = 0

    init {
        inflate(context, R.layout.custom_filterlayout, this)
        setupViewPager()
        setupTabForRecyclerView()
        calculateTabDimensions()
    }

    fun init(delegate: FilterLayoutContract) {
        this.delegate = delegate
        setupListeners()
    }

    private fun setupListeners() {
        okButton.setOnClickListener{
            delegate.onFilterDismiss()
        }

        dismissButton.setOnClickListener{
            delegate.onFilterDismiss()
        }
    }

    private fun setupTabForRecyclerView() {
        tabAdapterForViewPager = TabForViewPagerAdapter(object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                view_pager.setCurrentItem(position, true)
            }
        })
        tabAdapterForViewPager.customFilterLayoutTabList = list
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        viewpagerTabRecyclerview.apply {
            adapter = tabAdapterForViewPager
            layoutManager = linearLayoutManager
            suppressLayout(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    totalDxTabScroll += dx
                }
            })
        }
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPagerAdapter.adapterlist = list
        view_pager.adapter = viewPagerAdapter
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var currentPosition = 0
            var currentOffSet: Float = 0F

            private fun calculateScrollBy(offsetPercent: Float, position: Int): Int {
                val dx = (position + offsetPercent) * (tabAdapterForViewPager.viewHolderWidth) - totalDxTabScroll
                println("Calculate ScrollBy dx: $dx ")
                return dx.toInt()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                println("onPageScrolled: position: ${position}, positionOffSet: ${positionOffset}, positionOffSetPixels $positionOffsetPixels")
                viewpagerTabRecyclerview.let {
                    it.animateToPosition(calculateScrollBy(positionOffset, position))
                    if (position > currentPosition || positionOffset == 0.0F) {
                        currentPosition = position
                        currentOffSet = 0F
                        tabAdapterForViewPager.selectedItemIndex = position
                        it.suppressLayout(false)
                        tabAdapterForViewPager.notifyDataSetChanged()
                        it.suppressLayout(true)
                    }
                }
            }
        })
    }

    private fun generateTabItemViewHolderData(): List<CustomFilterLayoutTabItemElement> {
        val arrayList = ArrayList<CustomFilterLayoutTabItemElement>()
        for (i in 1..6) {
            arrayList.add(CustomFilterLayoutTabItemElement("Tab $i", "Visualization $i", false))
        }
        return arrayList
    }

    private fun RecyclerView.animateToPosition(dx: Int) {

        suppressLayout(false)
        scrollBy(dx, 0)
        suppressLayout(true)
    }

    fun prepareToOpenAnimation() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(buttonsContainer)
        okButton.isVisible = false
        constraintSet.clear(okButton.id, ConstraintSet.START)
        constraintSet.clear(okButton.id, ConstraintSet.END)
        okButton.x = this.width / 2f
        dismissButton.isVisible = false
        constraintSet.clear(dismissButton.id, ConstraintSet.START)
        constraintSet.clear(dismissButton.id, ConstraintSet.END)
        dismissButton.x = this.width / 2f
        constraintSet.applyTo(buttonsContainer)

    }

    private fun animateOpening(progress: Float) {
        okButton.isVisible = true
        okButton.alpha = progress
        dismissButton.isVisible = true
        dismissButton.alpha= progress
        okButton.x = width / 2 + (width / 4) * (progress)
        dismissButton.x = width / 2 - (width / 4) * (progress)
        viewpagerTabRecyclerview.layoutParams.height = (tabMaxHeight * progress).toInt()
        layoutParams.height = withoutTabsHeight + (tabMaxHeight * progress).roundToInt()
        println("With Tab Height: ${layoutParams.height}")

        requestLayout()
    }

    private fun calculateTabDimensions() {
        isVisible = true
        doOnLayout {
            withoutTabsHeight = it.measuredHeight
            isVisible = false
        }
    }

    fun animateExpansion(isForward: Boolean): ValueAnimator {
        return ExpandCollapseAnimationDelegate.getValueAnimator(isForward, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            animateOpening(progress)
        }

    }

    override fun onFilterItemClicked(pagePosition: Int, viewPagerItemsSelectionMap: Map<Int, List<Int>>) {
        if (viewPagerItemsSelectionMap[pagePosition]?.isEmpty()!!){
        tabAdapterForViewPager.customFilterLayoutTabList[pagePosition].hasSelections = false
        }else if (tabAdapterForViewPager.customFilterLayoutTabList[pagePosition].hasSelections.not()){
            tabAdapterForViewPager.customFilterLayoutTabList[pagePosition].hasSelections = true
        }
        tabAdapterForViewPager.notifyDataSetChanged()
    }
}

interface ViewPagerItemsContract{
    fun onFilterItemClicked(pagePosition: Int, viewPagerItemsSelectionMap: Map<Int, List<Int>>)
}

interface FilterLayoutContract {
    fun onFilterDismiss()
    fun onFilterConfirmed()
}

