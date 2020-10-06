package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Point
import android.util.Log
import android.util.TypedValue
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.common.Utils
import kotlinx.android.synthetic.main.expand_collapse_viewholder.view.*

val Context.screenHeight: Int
    get() {
        return Point().also { display?.getSize(it) }.y
    }

class ExpandCollapseAnimationDelegate(context: Context) {
    private val Int.dp: Int
        get() {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
        }
    private val Context.screenWidth: Int
        get() {
            return Point().also { display?.getSize(it) }.x
        }
    private lateinit var recyclerView: RecyclerView
    private var originalHeight: Int = -1
    private var additionalHeight: Int = -1
    private var expandedPosition: Int = -1
    private var toBeCollapsedPosition: Int = -1
    private val ORIGINALWIDTH: Int = context.screenWidth - 48.dp
    private val ADDITIONAL_WIDTH: Int = 32.dp

    fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun onClick(position: Int) {
        if (expandedPosition < 0) {
            expandViewHolderAtPosition(position)
        } else {
            if (position.compareTo(expandedPosition) == 0) {
                toBeCollapsedPosition = expandedPosition
                collapseViewHolderAtPosition(toBeCollapsedPosition)
                toBeCollapsedPosition = -1
                expandedPosition = -1
            } else {
                if (recyclerView.findViewHolderForAdapterPosition(expandedPosition) != null) {
                    toBeCollapsedPosition = expandedPosition
                    collapseViewHolderAtPosition(toBeCollapsedPosition)
                    expandedPosition = position
                    expandViewHolderAtPosition(expandedPosition)
                } else {
                    toBeCollapsedPosition = expandedPosition
                    expandedPosition = position
                    expandViewHolderAtPosition(expandedPosition)
                }

            }
        }
    }

    fun onViewAttachedToWindow(holder: ExpandCollapseViewHolder) {
        if (holder.adapterPosition.compareTo(toBeCollapsedPosition) == 0) {
            resetHeight(holder)
            toBeCollapsedPosition = -1
        }
    }

    private fun resetHeight(holder: ExpandCollapseViewHolder) {
        holder.containerView.container.layoutParams.height = originalHeight
        holder.containerView.container.layoutParams.width = ORIGINALWIDTH
        holder.containerView.requestLayout()
    }

    fun calculateMeasures(holder: ExpandCollapseViewHolder) {
        if (holder.adapterPosition.equals(expandedPosition)) {

        } else {
            holder.containerView.container.layoutParams.width = ORIGINALWIDTH
        }
        if (additionalHeight < 0) {

            holder.containerView.doOnLayout { container ->
                val container = container.container
                val additionalViewContainer = container.additionalViewContainer
                additionalHeight = 0
                originalHeight = container.height
                additionalViewContainer.isVisible = true

                additionalViewContainer.doOnPreDraw { view ->
                    additionalHeight = view.height + view.marginTop + view.marginBottom
                    view.isVisible = false
                }
            }
        }
    }

    private fun expandViewHolderAtPosition(position: Int) {
        expandedPosition = position

        val holder = recyclerView.findViewHolderForAdapterPosition(position) as ExpandCollapseViewHolder

        val animator = getValueAnimator(true, 600L, AccelerateDecelerateInterpolator()) { progress ->
            holder.itemView.container.layoutParams.height = originalHeight + ((additionalHeight) * progress).toInt()
            holder.itemView.chevron.rotation = 90 * progress
            holder.itemView.container.layoutParams.width = ORIGINALWIDTH + ((ADDITIONAL_WIDTH) * (progress)).toInt()
            holder.itemView.container.backgroundTintList = ColorStateList.valueOf(Utils.mergeColors(
                    recyclerView.resources.getColor(R.color.container_collapsed),
                    recyclerView.resources.getColor(R.color.expanded_foreground_color),
                    1 - progress
            ))//            Log.d("Expanding", "additionalHeight:" + additionalHeight.toString())
//            Log.d("Expanding", "originalHeight:" + originalHeight.toString())
            holder.itemView.container.requestLayout()

        }

        animator.doOnStart { holder.containerView.additionalViewContainer.isVisible = true }
        animator.start()
    }

    private fun collapseViewHolderAtPosition(position: Int) {
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as ExpandCollapseViewHolder

        val animator = getValueAnimator(true, 300L, AccelerateDecelerateInterpolator()) { progress ->
            holder.itemView.container.layoutParams.height = originalHeight + ((additionalHeight) * (1 - progress)).toInt()
            holder.itemView.chevron.rotation = 90 * (1 - progress)
            holder.itemView.container.layoutParams.width = ORIGINALWIDTH + ((ADDITIONAL_WIDTH) * (1 - progress)).toInt()
            holder.itemView.container.backgroundTintList = ColorStateList.valueOf(Utils.mergeColors(
                    recyclerView.resources.getColor(R.color.container_collapsed),
                    recyclerView.resources.getColor(R.color.expanded_foreground_color),
                    progress
            ))


            Log.d("Collapsing", "progress: $progress")
            holder.itemView.container.requestLayout()
        }
        animator.doOnEnd { holder.containerView.additionalViewContainer.isVisible = false }
        animator.start()
    }

    val LinearLayoutManager.visibleItensRange
    get() = findFirstVisibleItemPosition() .. findLastVisibleItemPosition()

    fun escaleDown(forward: Boolean): ValueAnimator {

        return getValueAnimator(forward, 1000L, AccelerateDecelerateInterpolator()) {progress ->
            for (visiblePosition in (recyclerView.layoutManager as LinearLayoutManager).visibleItensRange) {
                val holder = recyclerView.findViewHolderForAdapterPosition(visiblePosition) as ExpandCollapseViewHolder
                holder.itemView.container.scaleX = 1 - 0.1f*progress
                holder.itemView.container.scaleY = 1 - 0.05f*progress
            }
        }
    }

    companion object {
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
    }

}
