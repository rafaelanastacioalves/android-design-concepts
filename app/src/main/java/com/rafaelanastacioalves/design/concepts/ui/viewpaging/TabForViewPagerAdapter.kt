package com.rafaelanastacioalves.design.concepts.ui.viewpaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.viewpager_recyclerview_tab_viewholder.view.*

class TabForViewPagerAdapter(val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<TabViewHolder>() {
    var selectedItemIndex: Int = 0
    lateinit var tabList: List<TabItemElement>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        return TabViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewpager_recyclerview_tab_viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        return if (tabList.isNullOrEmpty()) {
            0
        } else {
            tabList.size
        }
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        if (selectedItemIndex == position){
            holder.itemView.textView.text = tabList[position].textPage + "*"
        }else{
            holder.itemView.textView.text = tabList[position].textPage
        }

        if (tabList[position].hasSelectinos){
            var text = holder.itemView.textView.text
            holder.itemView.textView.text = text.toString() + "[X]"
        }
        holder.itemView.setOnClickListener({ v ->
            recyclerViewClickListener.onClick(v, position)
            selectedItemIndex = position
        })
    }

}

class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}
