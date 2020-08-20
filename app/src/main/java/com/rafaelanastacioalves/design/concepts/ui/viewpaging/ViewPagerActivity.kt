package com.rafaelanastacioalves.design.concepts.ui.viewpaging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.home_activity.*
import kotlinx.android.synthetic.main.viewpager_activity.*
import kotlinx.android.synthetic.main.viewpager_item_viewholder.view.*

class ViewPagerActivity : AppCompatActivity() {
    val list = generateTabItemViewHolderData()
    lateinit var tabForViewPagerAdapter: TabForViewPagerAdapter
    lateinit var viewPagerAdapter: ViewPagerAdapter
    var totalDxScroll: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewpager_activity)
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
        viewpager_tab_recyclerview.adapter = tabForViewPagerAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = HORIZONTAL
        viewpager_tab_recyclerview.layoutManager = linearLayoutManager
        viewpager_tab_recyclerview.suppressLayout(true)
        viewpager_tab_recyclerview.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalDxScroll = +dx
            }
        })
    }

    private fun generateTabItemViewHolderData(): List<TabItemElement> {
        val arrayList = ArrayList<TabItemElement>()
        for (i in 1..6) {
            arrayList.add(TabItemElement("Tab ${i}", "Visualization ${i}", false))
        }
        return arrayList
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
        view_pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            var currentPosition = 0
            var currentAbsoluteOffset = 0

            private fun calculateDx(position: Int): Int {

                var dx = 0
                dx =+ (tabForViewPagerAdapter.viewHolderWidth + tabForViewPagerAdapter.viewHolderOffSet)*(position)
                return dx
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (position != currentPosition){
                    viewpager_tab_recyclerview.animateToPosition(position, calculateDx(
                            if (position > currentPosition) 1 else -1))
                    currentPosition = position
                    tabForViewPagerAdapter.selectedItemIndex = position
                    tabForViewPagerAdapter.notifyDataSetChanged()


                }


            }
        })
    }

    private fun updateAdapters() {
        tabForViewPagerAdapter.tabList = list
        tabForViewPagerAdapter.notifyDataSetChanged()
        viewPagerAdapter.adapterlist = list
        viewPagerAdapter.notifyDataSetChanged()

    }

}

private fun RecyclerView.animateToPosition(position: Int, dx: Int) {
    suppressLayout(false)
    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 20)
    suppressLayout(true)
//    scrollBy(dx, 0)
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
        holder.itemView.button.setOnClickListener({ v-> recyclerViewClickListener.onClick(
                v, position
        ) })
    }

}

class SampleViewHolder(itemView: View) : ViewHolder(itemView) {

}
