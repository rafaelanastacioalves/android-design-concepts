package com.example.rafaelanastacioalves.moby.entitymainlisting;

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rafaelanastacioalves.moby.R
import com.example.rafaelanastacioalves.moby.domain.entities.MainEntity
import com.example.rafaelanastacioalves.moby.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.detail_entity_viewholder.view.*

class MainEntityAdapter(context: Context) : RecyclerView.Adapter<MainEntityViewHolder>() {
    lateinit private var recyclerViewClickListener: RecyclerViewClickListener
    private var items: List<MainEntity>? = null

    private val mContext: Context = context

    private var expandedPosition: Int = -1

    fun setRecyclerViewClickListener(aRVC: RecyclerViewClickListener ) {
        this.recyclerViewClickListener = aRVC;
    }

    fun getItems(): List<MainEntity>? {
        return this.items;
    }
    fun setItems(items: List<MainEntity>?) {
        this.items = items as ArrayList<MainEntity>;
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainEntityViewHolder  {
        return MainEntityViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_entity_viewholder, parent, false), recyclerViewClickListener);
    }


    override fun onBindViewHolder(holder: MainEntityViewHolder, position: Int ) {

        val aRepoW = getItems()?.get(position) as MainEntity
        holder.bind(aRepoW, mContext)
        holder.containerView.doOnLayout {view ->
            val container = view.detail_container
            val additionalViewContainer = view.additionalViewContainer

            val originalHeight = container.measuredHeight
            additionalViewContainer.isVisible = true

            additionalViewContainer.doOnNextLayout { view ->
                val additionalHeight = view.measuredHeight
                view.post { view.isVisible = false }
            }
        }
        holder.containerView.setOnClickListener { view ->

            if (expandedPosition < 0){
                measureExpandViewHolderAtPosition(position)
            }else{
                if (position.compareTo(expandedPosition) == 0) {
                    measureCollapseViewHolderAtPosition(expandedPosition)
                    expandedPosition = -1
                }else {
                    measureCollapseViewHolderAtPosition(expandedPosition)
                    measureExpandViewHolderAtPosition(position)
                }
            }

        }
    }

    private fun measureExpandViewHolderAtPosition(position: Int) {
        expandedPosition = position
        Toast.makeText(mContext, "Expanding item at position $position", Toast.LENGTH_SHORT).show()
    }

    private fun measureCollapseViewHolderAtPosition(position: Int) {
        Toast.makeText(mContext, "Collapsing item at position $position", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        if (getItems() != null){
            return getItems()!!.size;
        }else{
            return 0;
        }
    }
}

