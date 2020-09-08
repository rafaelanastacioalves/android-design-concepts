package com.rafaelanastacioalves.design.concepts.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import com.rafaelanastacioalves.design.concepts.ui.viewpaging.TabForViewPagerAdapter
import com.rafaelanastacioalves.design.concepts.ui.viewpaging.TabItemElement
import kotlinx.android.synthetic.main.customview_filterlayout.view.*
import kotlinx.android.synthetic.main.viewpager_item_viewholder.view.*

class FilterLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    val list = generateTabItemViewHolderData()
    lateinit var tabForViewPagerAdapter: TabForViewPagerAdapter
    lateinit var viewPagerAdapter: ViewPagerAdapter
    var totalDxTabScroll: Int = 0

    init{
        inflate(context, R.layout.customview_filterlayout, this)
        setupViewPager()
        setupTabForRecyclerView()

    }

    private fun setupTabForRecyclerView() {
        tabForViewPagerAdapter = TabForViewPagerAdapter(object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                view_pager.setCurrentItem(position, true)
            }
        })
        tabForViewPagerAdapter.tabList = list
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        viewpager_tab_recyclerview.apply {
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

    override fun onFinishInflate() {
        super.onFinishInflate()

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
                viewpager_tab_recyclerview.let {
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
        tabForViewPagerAdapter.tabList = list
        viewpager_tab_recyclerview.suppressLayout(false)
        tabForViewPagerAdapter.notifyDataSetChanged()
        viewpager_tab_recyclerview.suppressLayout(true)
        viewPagerAdapter.adapterlist = list
        viewPagerAdapter.notifyDataSetChanged()

    }

    private fun generateTabItemViewHolderData(): List<TabItemElement> {
        val arrayList = ArrayList<TabItemElement>()
        for (i in 1..6) {
            arrayList.add(TabItemElement("Tab ${i}", "Visualization ${i}", false))
        }
        return arrayList
    }

    private fun RecyclerView.animateToPosition(position: Int, dx: Int) {

        suppressLayout(false)
        scrollBy(dx, 0)
        suppressLayout(true)
    }


}

class ViewPagerAdapter(val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<SampleViewHolder>() {
    lateinit var adapterlist: List<TabItemElement>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewpager_item_viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        return if (adapterlist.isNullOrEmpty()) {
            0
        }else{
            adapterlist.size
        }
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.itemView.textView.text = adapterlist.get(position).textPage
        holder.itemView.button.setOnClickListener { v ->
            recyclerViewClickListener.onClick(
                    v, position
            )
        }
    }
}

class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

