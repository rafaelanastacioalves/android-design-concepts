package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.common.Utils
import com.rafaelanastacioalves.design.concepts.domain.entities.CustomFilterLayoutTabItemElement
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.custom_filterlayout.view.*

class CustomFilterLayoutHandler(private val button_background: View,
                                private val viewpagerTabRecyclerview: RecyclerView,
                                private val filterViewPager: ViewPager2) : ViewPagerFilterItemsContract {

    private val context: Context? = viewpagerTabRecyclerview.context
    private val resources = viewpagerTabRecyclerview.context.resources

    var totalDxTabScroll: Int = 0
    val list = generateTabItemViewHolderData()

    private lateinit var tabAdapterForViewPager: TabForViewPagerAdapter
    private val customFilterLayoutViewPagerAdapter: CustomFilterLayoutViewPagerAdapter = CustomFilterLayoutViewPagerAdapter(this)

    init {
        customFilterLayoutViewPagerAdapter.adapterlist = list
        filterViewPager.adapter = customFilterLayoutViewPagerAdapter
        filterViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var currentPosition = 0
            var currentOffSet: Float = 0F

            lateinit var currentHolder: TabViewHolder
            lateinit var nextHolder: TabViewHolder

            private fun calculateScrollBy(offsetPercent: Float, position: Int): Int {
                val dx = (position + offsetPercent) * (tabAdapterForViewPager.viewHolderWidth) - totalDxTabScroll
//                println("Calculate ScrollBy dx: $dx ")
                return dx.toInt()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
//                println("onPageScrolled: position: ${position}, positionOffSet: ${positionOffset}, positionOffSetPixels $positionOffsetPixels")
                viewpagerTabRecyclerview.apply {
                    animateToPosition(calculateScrollBy(positionOffset, position))
                    //TODO: Refatorar essa parte ta muito pesado ...
                    currentHolder = tabRecyclerview
                            .findViewHolderForAdapterPosition(position) as TabViewHolder? ?: return
                    nextHolder = tabRecyclerview
                            .findViewHolderForAdapterPosition(position + 1) as TabViewHolder
                    suppressLayout(false)
                    currentHolder.setPercentActivated(1 - positionOffset)
                    nextHolder.setPercentActivated(positionOffset)
                    suppressLayout(true)
                    if (position > currentPosition || positionOffset == 0F) {
                        currentPosition = position
                        currentOffSet = 0F
                        tabAdapterForViewPager.selectedItemIndex = position
                        suppressLayout(false)
                        tabAdapterForViewPager.notifyDataSetChanged()
                        suppressLayout(true)
                    }
                }
            }
        })

        tabAdapterForViewPager = TabForViewPagerAdapter(object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                filterViewPager.setCurrentItem(position, true)
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


    override fun onFilterItemClicked(pagePosition: Int, viewPagerItemsSelectionMap: Map<Int, List<Int>>) {
        if (viewPagerItemsSelectionMap[pagePosition]?.isEmpty()!!) {
            tabAdapterForViewPager.customFilterLayoutTabList[pagePosition].hasSelections = false
            tabAdapterForViewPager.isToAnimateBadge = true
            updateBottom(viewPagerItemsSelectionMap)

            viewpagerTabRecyclerview.suppressLayout(false)
            tabAdapterForViewPager.notifyItemChanged(pagePosition, tabAdapterForViewPager.UPDATEBADGE)
            viewpagerTabRecyclerview.suppressLayout(true)
        } else if (tabAdapterForViewPager.customFilterLayoutTabList[pagePosition].hasSelections.not()) {
            tabAdapterForViewPager.customFilterLayoutTabList[pagePosition].hasSelections = true
            tabAdapterForViewPager.isToAnimateBadge = true
            updateBottom(viewPagerItemsSelectionMap)

            viewpagerTabRecyclerview.suppressLayout(false)
            tabAdapterForViewPager.notifyItemChanged(pagePosition, tabAdapterForViewPager.UPDATEBADGE)
            viewpagerTabRecyclerview.suppressLayout(true)
        }

    }

    private fun updateBottom(viewPagerItemsSelectionMap: Map<Int, List<Int>>) {
        viewPagerItemsSelectionMap.forEach {
            if (it.value.isNullOrEmpty().not()) {
                if (button_background.isActivated) return else {
                    animateBottom(hasSelections = true)
                }
            }
        }
        if (button_background.isActivated.not()) return else {
            animateBottom(hasSelections = false)
        }
    }

    private fun animateBottom(hasSelections: Boolean) {
        val valueAnimator = Utils.getValueAnimator(hasSelections, 100, AccelerateInterpolator()) { progress ->

            button_background.backgroundTintList = ColorStateList.valueOf(
                    Utils.mergeColors(
                            resources.getColor(R.color.bottom_bar_color),
                            resources.getColor(R.color.colorAccent),
                            progress
                    )
            )
        }
        valueAnimator.doOnEnd {
            button_background.isActivated = hasSelections
        }
        valueAnimator.start()
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


}