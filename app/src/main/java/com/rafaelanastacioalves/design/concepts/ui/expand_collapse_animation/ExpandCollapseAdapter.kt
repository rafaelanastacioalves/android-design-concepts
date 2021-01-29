package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.ValueAnimator
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.domain.entities.FakeData
import kotlinx.android.synthetic.main.expand_collapse_viewholder.view.*


class ExpandCollapseAdapter(activity: Activity) : RecyclerView.Adapter<ExpandCollapseViewHolder>() {
    private var items: List<FakeData>? = null
    private val animator: ExpandCollapseAnimationDelegate = ExpandCollapseAnimationDelegate(activity)

    private fun getItems(): List<FakeData>? {
        return this.items
    }

    fun setItems(items: List<FakeData>?) {
        this.items = items as ArrayList<FakeData>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandCollapseViewHolder {
        return ExpandCollapseViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.expand_collapse_viewholder, parent, false))
    }

    override fun onViewAttachedToWindow(holder: ExpandCollapseViewHolder) {
        super.onViewAttachedToWindow(holder)
        animator.onViewAttachedToWindow(holder)
    }

    override fun getItemCount(): Int {
        return if (getItems() != null){
            getItems()!!.size
        }else{
            0
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        animator.onAttachedToRecyclerView(recyclerView)
    }

    fun holdersScaleDownAnimator(forward: Boolean): ValueAnimator {
        return animator.escaleDown(forward)
    }

    override fun onBindViewHolder(holder: ExpandCollapseViewHolder, position: Int ) {
        getItems()?.get(position) as FakeData
        animator.calculateMeasures(holder)
        holder.containerView.container.setOnClickListener {
            animator.onClick(position)
        }
    }
}

