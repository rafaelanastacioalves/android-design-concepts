package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.domain.entities.CustomFilterLayoutTabItemElement
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener

class TabForViewPagerAdapter(val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<TabViewHolder>() {

    var viewHolderWidth: Float = 0F
    var selectedItemIndex: Int = 0

    enum class ViewHolderType {NORMAL, FOOTER_VIEW}
    lateinit var customFilterLayoutTabList: List<CustomFilterLayoutTabItemElement>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        if (viewHolderWidth == 0F) {
            viewHolderWidth = parent.context.resources.getDimension(R.dimen.viewpageging_tab_item_margin_horizonal)
        }
        return TabViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_filterlayout_viewpager_recyclerview_tab_viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        return if (customFilterLayoutTabList.isNullOrEmpty()) {
            0
        } else {
            customFilterLayoutTabList.size + 3
        }
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        if (holder.itemViewType == ViewHolderType.NORMAL.ordinal) {
            if (selectedItemIndex == position){
                markAsCurrent(holder,true)
            }else{
                markAsCurrent(holder, false)
            }

            if (customFilterLayoutTabList[position].hasSelections){
                markHasSelections(holder,true)
            }
            holder.itemView.setOnClickListener({ v ->
                recyclerViewClickListener.onClick(v, position)
                selectedItemIndex = position
            })
        }
    }

    private fun markHasSelections(holder: TabViewHolder, marked: Boolean) {
        Log.d(javaClass.name, "Marking TabView has selections in position ${holder.adapterPosition}: $marked")
    }

    private fun markAsCurrent(holder: TabViewHolder, isCurrent: Boolean) {
        if (isCurrent){
            Log.d(javaClass.name, "TabView of position ${holder.adapterPosition} is the current")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < customFilterLayoutTabList.size) ViewHolderType.NORMAL.ordinal else ViewHolderType.FOOTER_VIEW.ordinal

    }

    override fun onViewAttachedToWindow(holder: TabViewHolder) {
        super.onViewAttachedToWindow(holder)
        if(holder.itemViewType == ViewHolderType.FOOTER_VIEW.ordinal){
            holder.itemView.visibility = View.INVISIBLE
            holder.itemView.minimumWidth = 200
        }
    }
}

class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}


