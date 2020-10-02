package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.domain.entities.CustomFilterLayoutTabItemElement
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation.ExpandCollapseAnimationDelegate
import kotlinx.android.synthetic.main.custom_filterlayout_viewpager_recyclerview_tab_viewholder.view.*

class TabForViewPagerAdapter(val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<TabViewHolder>() {

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
            if (selectedItemIndex == position){
                markAsCurrent(holder,true)
            }else{
                markAsCurrent(holder, false)
            }

            customFilterLayoutTabList[position].hasSelections.let{
                if (isToAnimateBadge) {
                    markHasSelections(holder, it)
                    isToAnimateBadge = false
                } else {
                    holder.makeBadgeVisible(it)
                }
            }
            holder.itemView.setOnClickListener({ v ->
                recyclerViewClickListener.onClick(v, position)
                selectedItemIndex = position
            })
        }
    }

    override fun onViewDetachedFromWindow(holder: TabViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.makeBadgeVisible(false)

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
        val valueAnimator = ExpandCollapseAnimationDelegate.getValueAnimator(isToMark, 100L, AccelerateDecelerateInterpolator()) { progress ->
            itemView.tabBadge.scaleX = progress
            itemView.tabBadge.scaleY = progress
        }
        if (isToMark) {
            valueAnimator.doOnStart {
                itemView.tabBadge.isVisible = true
            }
        } else {
            valueAnimator.doOnEnd {
                itemView.tabBadge.isVisible = false
            }
        }
        valueAnimator.start()
    }

    fun makeBadgeVisible(visibility: Boolean) {
        itemView.tabBadge.isVisible = visibility
    }

    fun isBadgeVisible(): Boolean {
        return itemView.tabBadge.isVisible
    }

}


