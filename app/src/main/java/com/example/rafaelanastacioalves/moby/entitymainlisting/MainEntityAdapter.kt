package com.example.rafaelanastacioalves.moby.entitymainlisting

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rafaelanastacioalves.moby.R
import com.example.rafaelanastacioalves.moby.domain.entities.FakeData
import kotlinx.android.synthetic.main.main_entity_viewholder.view.*


class MainEntityAdapter(context: Context ) : RecyclerView.Adapter<MainEntityViewHolder>() {
    private var items: List<FakeData>? = null
    private val mContext: Context = context
    private val animator: ExpandAnimationDelegate = ExpandAnimationDelegate()

    private fun getItems(): List<FakeData>? {
        return this.items
    }
    fun setItems(items: List<FakeData>?) {
        this.items = items as ArrayList<FakeData>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainEntityViewHolder  {
        return MainEntityViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.main_entity_viewholder, parent, false))
    }

    override fun onViewAttachedToWindow(holder: MainEntityViewHolder) {
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

    override fun onBindViewHolder(holder: MainEntityViewHolder, position: Int ) {

        val aRepoW = getItems()?.get(position) as FakeData
        holder.bind(aRepoW, mContext)

        animator.calculateMeasures(holder)
        holder.containerView.detail_container.setOnClickListener {
            animator.onClick(position)
        }
    }


}

