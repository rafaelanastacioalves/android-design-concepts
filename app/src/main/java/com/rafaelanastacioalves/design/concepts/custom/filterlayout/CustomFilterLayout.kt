package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation.ExpandCollapseAnimationDelegate
import kotlinx.android.synthetic.main.custom_filterlayout.view.*
import kotlin.math.roundToInt

class FilterLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tabMaxHeight by lazy { resources.getDimension(R.dimen.customLayoutTabMaxHeight) }
    var withoutTabsHeight: Int = 0
    private val windowsWidth = Point().also {
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.getSize(it)
    }.x
    private lateinit var delegate: FilterLayoutContract
    val list = generateTabItemViewHolderData()
    lateinit var tabForViewPagerAdapter: TabForViewPagerAdapter
    lateinit var viewPagerAdapter: ViewPagerAdapter
    var totalDxTabScroll: Int = 0

    init {
        inflate(context, R.layout.custom_filterlayout, this)
        setupViewPager()
        setupTabForRecyclerView()
        calculateTabDimensions()
    }

    public fun init(delegate: FilterLayoutContract): Unit {
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
        tabForViewPagerAdapter = TabForViewPagerAdapter(object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                view_pager.setCurrentItem(position, true)
            }
        })
        tabForViewPagerAdapter.customFilterLayoutTabList = list
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        viewpagerTabRecyclerview.apply {
            adapter = tabForViewPagerAdapter
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
        viewPagerAdapter = ViewPagerAdapter(object : RecyclerViewClickListener{
            override fun onClick(view: View, position: Int) {
                list.get(position).hasSelectinos = list.get(position).hasSelectinos.not()
                updateAdapters()
            }

        })
        viewPagerAdapter.adapterlist = list
        view_pager.adapter = viewPagerAdapter
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var currentPosition = 0
            var currentAbsoluteOffset = 0
            var currentOffSet: Float = 0F

            private fun calculateScrollBy(offsetPercent: Float, position: Int): Int {
                var dx = (position + offsetPercent) * (tabForViewPagerAdapter.viewHolderWidth) - totalDxTabScroll
                println("Calculate ScrollBy dx: ${dx} ")
                return dx.toInt()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                println("onPageScrolled: position: ${position}, positionOffSet: ${positionOffset}, positionOffSetPixels ${positionOffsetPixels}")
                viewpagerTabRecyclerview.let {
                    it.animateToPosition(position, calculateScrollBy(positionOffset, position))
                    if (position > currentPosition || positionOffset == 0.0F) {
                        currentPosition = position
                        currentOffSet = 0F
                        tabForViewPagerAdapter.selectedItemIndex = position
                        it.suppressLayout(false)
                        tabForViewPagerAdapter.notifyDataSetChanged()
                        it.suppressLayout(true)
                    }
                }


            }
        })
    }

    private fun updateAdapters() {
        tabForViewPagerAdapter.customFilterLayoutTabList = list
        viewpagerTabRecyclerview.suppressLayout(false)
        tabForViewPagerAdapter.notifyDataSetChanged()
        viewpagerTabRecyclerview.suppressLayout(true)
        viewPagerAdapter.adapterlist = list
        viewPagerAdapter.notifyDataSetChanged()

    }

    private fun generateTabItemViewHolderData(): List<CustomFilterLayoutTabItemElement> {
        val arrayList = ArrayList<CustomFilterLayoutTabItemElement>()
        for (i in 1..6) {
            arrayList.add(CustomFilterLayoutTabItemElement("Tab ${i}", "Visualization ${i}", false))
        }
        return arrayList
    }

    private fun RecyclerView.animateToPosition(position: Int, dx: Int) {

        suppressLayout(false)
        scrollBy(dx, 0)
        suppressLayout(true)
    }

    fun preparetoOpenAnimation() {
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

    fun animateOpening(progress: Float) {
        okButton.isVisible = true
        dismissButton.isVisible = true
        okButton.x = width / 2 + (width / 4) * (progress)
        dismissButton.x = width / 2 - (width / 4) * (progress)
        viewpagerTabRecyclerview.layoutParams.height = (tabMaxHeight * progress).toInt()
        layoutParams.height = withoutTabsHeight + (tabMaxHeight * progress).roundToInt()
        println("With Tab Height: ${layoutParams.height}")

        requestLayout()
    }

    fun calculateTabDimensions() {
        isVisible = true
        doOnLayout {

            withoutTabsHeight = it.measuredHeight

            isVisible = false
        }
    }

    fun showTabs() {
        viewpagerTabRecyclerview.isVisible = true
    }

    fun animateExpansion(): ValueAnimator {
        val valueAnimator = ExpandCollapseAnimationDelegate.getValueAnimator(true, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            animateOpening(progress)
        }
        return valueAnimator

    }
}


class ViewPagerAdapter(val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<SampleViewHolder>() {
    lateinit var adapterlist: List<CustomFilterLayoutTabItemElement>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return if (viewType == 0) {
            SampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_filterlayout_viewpager_item_holder_3, parent, false))
        } else {
            SampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_filterlayout_viewpager_item_holder_1, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return if (adapterlist.isNullOrEmpty()) {
            0
        } else {
            adapterlist.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {

    }
}

class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

interface FilterLayoutContract {
    fun onFilterDismiss()
    fun onFilterConfirmed()
}

