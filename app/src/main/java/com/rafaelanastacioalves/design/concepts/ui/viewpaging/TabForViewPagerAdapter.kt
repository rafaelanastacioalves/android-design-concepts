package com.rafaelanastacioalves.design.concepts.ui.viewpaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.viewpager_recyclerview_tab_viewholder.view.*

class TabForViewPagerAdapter(val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<TabViewHolder>() {

    var viewHolderWidth: Int = 0
    var viewHolderOffSet: Int = 0
    var selectedItemIndex: Int = 0

    enum class ViewHolderType {NORMAL, FOOTER_VIEW}
    lateinit var tabList: List<TabItemElement>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        return TabViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewpager_recyclerview_tab_viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        return if (tabList.isNullOrEmpty()) {
            0
        } else {
            tabList.size + 2
        }
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        if (holder.itemViewType == ViewHolderType.NORMAL.ordinal) {
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

    override fun getItemViewType(position: Int): Int {
        return if (position < tabList.size) ViewHolderType.NORMAL.ordinal else ViewHolderType.FOOTER_VIEW.ordinal

    }

    override fun onViewAttachedToWindow(holder: TabViewHolder) {
        super.onViewAttachedToWindow(holder)
        viewHolderWidth =  if (viewHolderWidth == 0)  holder.itemView.measuredWidth else viewHolderWidth
        viewHolderOffSet = if (viewHolderOffSet == 0) holder.itemView.let { it.marginStart + it.marginEnd } else viewHolderOffSet
        if(holder.itemViewType == ViewHolderType.FOOTER_VIEW.ordinal){
            holder.itemView.visibility = View.INVISIBLE
            holder.itemView.minimumWidth = 200
        }
    }
}

class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}
