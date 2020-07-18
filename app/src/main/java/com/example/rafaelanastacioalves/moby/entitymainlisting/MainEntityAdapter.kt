package com.example.rafaelanastacioalves.moby.entitymainlisting;

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Debug
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.rafaelanastacioalves.moby.R
import com.example.rafaelanastacioalves.moby.domain.entities.MainEntity
import com.example.rafaelanastacioalves.moby.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.detail_entity_viewholder.view.*


class MainEntityAdapter(context: Context ) : RecyclerView.Adapter<MainEntityViewHolder>() {
    private lateinit var recyclerView: RecyclerView
    lateinit private var recyclerViewClickListener: RecyclerViewClickListener
    private var items: List<MainEntity>? = null

    private var originalHeight: Int = -1
    private var additionalHeight: Int = -1
    private val mContext: Context = context

    private var expandedPosition: Int = -1

    fun setRecyclerViewClickListener(aRVC: RecyclerViewClickListener) {
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
        if (additionalHeight < 0) {
            val action: (view: View) -> Unit = { view ->
                val container = view.detail_container
                val additionalViewContainer = view.additionalViewContainer
                additionalHeight = 0
                originalHeight = container.measuredHeight
                additionalViewContainer.isVisible = true

                additionalViewContainer.doOnPreDraw { view ->
                    additionalHeight = view.measuredHeight
                    view.post { view.isVisible = false }
                }
            }
            holder.containerView.doOnLayout(action)
        }
        holder.containerView.setOnClickListener { view ->

            if (expandedPosition < 0){
                expandViewHolderAtPosition(position)
            }else{
                if (position.compareTo(expandedPosition) == 0) {
                    collapseViewHolderAtPosition(expandedPosition)
                    expandedPosition = -1
                }else {
                    if (recyclerView.findViewHolderForAdapterPosition(expandedPosition)!=null){
                        collapseViewHolderAtPosition(expandedPosition)
                        expandViewHolderAtPosition(position)
                    }else{
                        expandViewHolderAtPosition(position)
                        expandedPosition = position
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
//        Toast.makeText(mContext, "Collapsing item at position $position", Toast.LENGTH_SHORT).show()
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

