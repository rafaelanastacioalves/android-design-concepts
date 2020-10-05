package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.domain.entities.CustomFilterLayoutTabItemElement
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.custom_filterlayout_viewpager_recyclerview_tab_viewholder.view.*

class TabForViewPagerAdapter(val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<TabViewHolder>() {

    val UPDATEBADGE: String = "update_badget"
    var viewHolderWidth: Float = 0F
    var selectedItemIndex: Int = 0

    enum class ViewHolderType {NORMAL, FOOTER_VIEW}

    lateinit var customFilterLayoutTabList: List<CustomFilterLayoutTabItemElement>
    var isToAnimateBadge = false
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
            if (selectedItemIndex == position) {
                markAsCurrent(holder, true)
            } else {
                markAsCurrent(holder, false)
            }
            customFilterLayoutTabList[position].hasSelections.let {
                holder.makeBadgeVisible(it)
            }
            holder.itemView.setOnClickListener({ v ->
                recyclerViewClickListener.onClick(v, position)
                selectedItemIndex = position
            })
        }
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int, payloads: MutableList<Any>) {
        payloads.firstOrNull() { it is String && it.equals(UPDATEBADGE) }
                ?: return super.onBindViewHolder(holder, position, payloads)
        customFilterLayoutTabList[position].hasSelections.let {
            if (isToAnimateBadge) {
                markHasSelections(holder, it)
                isToAnimateBadge = false
            } else {
                holder.makeBadgeVisible(it)
            }
        }
    }

    private fun markHasSelections(holder: TabViewHolder, isToMark: Boolean) {
        if (isToMark.xor(holder.isBadgeVisible())) {
            holder.animate(isToMark)
            Log.d(javaClass.name, "Marking TabView has selections in position ${holder.adapterPosition}: $isToMark")
        }
    }

    private fun markAsCurrent(holder: TabViewHolder, isCurrent: Boolean) {
        if (isCurrent) {
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
    fun animate(isToMark: Boolean) {
        itemView.tabBadge.animate()
                .setDuration(100L)
                .scaleX(if (isToMark) 1f else {
                    0f
                })
                .scaleY(if (isToMark) 1f else {
                    0f
                })
                .setInterpolator(OvershootInterpolator(3f))
                .start()

    }

    fun makeBadgeVisible(visible: Boolean) {
        itemView.tabBadge.scaleX = if (visible) 1f else 0f
        itemView.tabBadge.scaleY = if (visible) 1f else 0f
    }

    fun isBadgeVisible(): Boolean {
        return itemView.tabBadge.let { it.scaleY > 0 && it.scaleX > 0 }
    }

}


