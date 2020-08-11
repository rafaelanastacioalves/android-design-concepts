package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.domain.entities.FakeData
import kotlinx.android.synthetic.main.expand_collapse_viewholder.view.*


class ExpandCollapseAdapter(context: Context ) : RecyclerView.Adapter<ExpandCollapseViewHolder>() {
    private var items: List<FakeData>? = null
    private val mContext: Context = context
    private val animator: ExpandCollapseAnimationDelegate = ExpandCollapseAnimationDelegate(context)

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

    override fun onBindViewHolder(holder: ExpandCollapseViewHolder, position: Int ) {

        val aRepoW = getItems()?.get(position) as FakeData
        holder.bind(aRepoW, mContext)

        animator.calculateMeasures(holder)
        holder.containerView.detail_container.setOnClickListener {
            animator.onClick(position)
        }
    }


}

