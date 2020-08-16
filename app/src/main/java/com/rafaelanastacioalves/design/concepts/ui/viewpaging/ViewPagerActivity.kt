package com.rafaelanastacioalves.design.concepts.ui.viewpaging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.viewpager_activity.*
import kotlinx.android.synthetic.main.viewpager_viewholder.view.*

class ViewPagerActivity : AppCompatActivity() {
    val list = generateTabItemViewHolderData()
    lateinit var tabForViewPagerAdapter : TabForViewPagerAdapter
    lateinit var viewPagerAdapter : ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewpager_activity)
        setupViewPager()
        setupViewPagerTabWithRecyclerView()

    }

    private fun setupViewPagerTabWithRecyclerView() {
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
    }

    private fun generateTabItemViewHolderData(): List<TabItemElement> {
        val arrayList = ArrayList<TabItemElement>()
        for (i in 1..6) {
            arrayList.add(TabItemElement("Tab ${i}", "Visualization ${i}"))
        }
        return arrayList
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(object : RecyclerViewClickListener{
            override fun onClick(view: View, position: Int) {
                TODO("Not yet implemented")
            }

        })
        viewPagerAdapter.adapterlist = list
        view_pager.adapter = viewPagerAdapter
        view_pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabForViewPagerAdapter.selectedItemIndex = position
                viewpager_tab_recyclerview.scrollToPosition(position)
                tabForViewPagerAdapter.notifyDataSetChanged()

            }
        })
    }

}

class ViewPagerAdapter(param: RecyclerViewClickListener) : RecyclerView.Adapter<SampleViewHolder>() {

    lateinit var adapterlist : List<TabItemElement>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewpager_viewholder,parent, false))
    }


    override fun getItemCount(): Int {
        return if(adapterlist.isNullOrEmpty()){
            0
        }else{
            adapterlist.size
        }
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.itemView.textView.text = adapterlist.get(position).textPage
    }

}

class SampleViewHolder(itemView: View) : ViewHolder(itemView) {

}
