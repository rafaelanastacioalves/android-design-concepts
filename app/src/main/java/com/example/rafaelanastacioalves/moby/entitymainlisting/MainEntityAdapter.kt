package com.example.rafaelanastacioalves.moby.entitymainlisting;

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rafaelanastacioalves.moby.R
import com.example.rafaelanastacioalves.moby.domain.entities.FakeData
import com.example.rafaelanastacioalves.moby.domain.entities.MainEntity
import com.example.rafaelanastacioalves.moby.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.main_entity_viewholder.view.*


class MainEntityAdapter(context: Context ) : RecyclerView.Adapter<MainEntityViewHolder>() {
    private lateinit var recyclerView: RecyclerView
    private var items: List<FakeData>? = null

    private var originalHeight: Int = -1
    private var additionalHeight: Int = -1
    private val mContext: Context = context
    private var expandedPosition: Int = -1
    private var toBeCollapsedPosition: Int = -1




    fun getItems(): List<FakeData>? {
        return this.items;
    }
    fun setItems(items: List<FakeData>?) {
        this.items = items as ArrayList<FakeData>;
        notifyDataSetChanged();
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainEntityViewHolder  {
        return MainEntityViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_entity_viewholder, parent, false));
    }

    override fun onViewAttachedToWindow(holder: MainEntityViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.adapterPosition.compareTo(toBeCollapsedPosition) == 0){
            resetHeight(holder)
            toBeCollapsedPosition = -1
        }
    }

    private fun resetHeight(holder: MainEntityViewHolder) {
        holder.containerView.layoutParams.height = originalHeight
        holder.containerView.requestLayout()
    }

    override fun onBindViewHolder(holder: MainEntityViewHolder, position: Int ) {

        val aRepoW = getItems()?.get(position) as FakeData
        holder.bind(aRepoW, mContext)

        if (additionalHeight < 0) {

            holder.containerView.doOnLayout { container ->
                val container = container.detail_container
                val additionalViewContainer = container.additionalViewContainer
                additionalHeight = 0
                originalHeight = container.measuredHeight
                additionalViewContainer.isVisible = true

                additionalViewContainer.doOnPreDraw { view ->
                    additionalHeight = view.measuredHeight
                    view.post { view.isVisible = false }
                }
            }
        }
        holder.containerView.detail_container.setOnClickListener { view ->

            if (expandedPosition < 0){
                expandViewHolderAtPosition(position)
            }else{
                if (position.compareTo(expandedPosition) == 0) {
                    toBeCollapsedPosition = expandedPosition
                    collapseViewHolderAtPosition(toBeCollapsedPosition)
                    toBeCollapsedPosition = -1
                    expandedPosition = -1
                }else {
                    if (recyclerView.findViewHolderForAdapterPosition(expandedPosition)!=null){
                        toBeCollapsedPosition = expandedPosition
                        collapseViewHolderAtPosition(toBeCollapsedPosition)
                        expandedPosition = position
                        expandViewHolderAtPosition(expandedPosition)
                    }else{
                        toBeCollapsedPosition = expandedPosition
                        expandedPosition = position
                        expandViewHolderAtPosition(expandedPosition)
                        notifyItemChanged(toBeCollapsedPosition)
                    }

                }
            }

        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    private fun expandViewHolderAtPosition(position: Int) {
        expandedPosition = position

        val holder = recyclerView.findViewHolderForAdapterPosition(position) as MainEntityViewHolder

        val animator = getValueAnimator(true, 300L, AccelerateDecelerateInterpolator()) { progress ->
            holder.containerView.layoutParams.height = originalHeight + ((additionalHeight) * progress).toInt()
//            Log.d("Expanding", "additionalHeight:" + additionalHeight.toString())
//            Log.d("Expanding", "originalHeight:" + originalHeight.toString())

            holder.containerView.requestLayout()
        }
        animator.doOnStart { holder.containerView.additionalViewContainer.isVisible = true }
        animator.start()
        Toast.makeText(mContext, "Expanding item at position $position", Toast.LENGTH_SHORT).show()
    }

    private fun collapseViewHolderAtPosition(position: Int) {
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as MainEntityViewHolder

        val animator = getValueAnimator(true, 300L, AccelerateDecelerateInterpolator()) { progress ->
            holder.containerView.layoutParams.height = originalHeight + ((additionalHeight) * (1 - progress)).toInt()
            Log.d("Collapsing", "progress: " + progress.toString())
            holder.containerView.requestLayout()
        }
        animator.doOnEnd { holder.containerView.additionalViewContainer.isVisible = false }
        animator.start()
        Toast.makeText(mContext, "Collapsing item at position $position", Toast.LENGTH_SHORT).show()
    }

    inline fun getValueAnimator(
            forward: Boolean = true,
            duration: Long,
            interpolator: TimeInterpolator,
            crossinline updateListener: (progress: Float) -> Unit
    ): ValueAnimator {
        val a =
                if (forward) ValueAnimator.ofFloat(0f, 1f)
                else ValueAnimator.ofFloat(1f, 0f)
        a.addUpdateListener { updateListener(it.animatedValue as Float) }
        a.duration = duration
        a.interpolator = interpolator
        return a
    }

    override fun getItemCount(): Int {
        if (getItems() != null){
            return getItems()!!.size;
        }else{
            return 0;
        }
    }
}

