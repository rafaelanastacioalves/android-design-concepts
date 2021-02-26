
package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Point
import android.util.Log
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.common.dp
import com.rafaelanastacioalves.design.concepts.common.getValueAnimator
import com.rafaelanastacioalves.design.concepts.common.mergeColors
import kotlinx.android.synthetic.main.expand_collapse_viewholder.*
import kotlinx.android.synthetic.main.expand_collapse_viewholder.view.*

@Suppress("DEPRECATION")
val Context.screenHeight: Int
    get() = Point().also { (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(it) }.y

@Suppress("DEPRECATION")
public val Context.screenWidth: Int
    get() = Point().also { (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(it) }.x


class ExpandCollapseAnimationDelegate(val activity: Activity) {
    private var originalHeight: Int = -1
    private var additionalHeight: Int = -1
    private var expandedPosition: Int = -1
    private var toBeCollapsedPosition: Int = -1
    private val originalWidth: Int = activity.screenWidth - 48.dp
    private val additionalWidth: Int = 32.dp



    //            return Point().also { display?.getSize(it) }.x
    private lateinit var recyclerView: RecyclerView

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
        holder.containerView.container.layoutParams.width = originalWidth
        holder.containerView.requestLayout()
    }

    fun calculateMeasures(holder: ExpandCollapseViewHolder) {
        if ((holder.adapterPosition == expandedPosition).not()) {
            holder.containerView.container.layoutParams.width = originalWidth
        }
        if (additionalHeight < 0) {

            holder.containerView.doOnLayout { containerView ->
                val container = containerView.container
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
            holder.itemView.apply {
                container.layoutParams.height = originalHeight + ((additionalHeight) * progress).toInt()
                chevron.rotation = 90 * progress
                container.layoutParams.width = originalWidth + ((additionalWidth) * (progress)).toInt()
                container.backgroundTintList = ColorStateList.valueOf(mergeColors(
                        recyclerView.resources.getColor(R.color.expanded_foreground_color),
                        recyclerView.resources.getColor(R.color.container_collapsed),

                        1 - progress
                ))//            Log.d("Expanding", "additionalHeight:" + additionalHeight.toString())
//            Log.d("Expanding", "originalHeight:" + originalHeight.toString())
                container.requestLayout()
            }

        }

        animator.doOnStart { holder.containerView.additionalViewContainer.isVisible = true }
        animator.start()
    }

    private fun collapseViewHolderAtPosition(position: Int) {
        val holder = recyclerView.findViewHolderForAdapterPosition(position) as ExpandCollapseViewHolder

        val animator = getValueAnimator(true, 300L, AccelerateDecelerateInterpolator()) { progress ->
            holder.itemView.apply {
                container.layoutParams.height = originalHeight + ((additionalHeight) * (1 - progress)).toInt()
                chevron.rotation = 90 * (1 - progress)
                container.layoutParams.width = originalWidth + ((additionalWidth) * (1 - progress)).toInt()
                container.backgroundTintList = ColorStateList.valueOf(mergeColors(
                        recyclerView.resources.getColor(R.color.expanded_foreground_color),
                        recyclerView.resources.getColor(R.color.container_collapsed),
                        progress
                ))

                Log.d("Collapsing", "progress: $progress")
                holder.itemView.container.requestLayout()
            }
        }
        animator.doOnEnd { holder.containerView.additionalViewContainer.isVisible = false }
        animator.start()
    }

    private val LinearLayoutManager.visibleItensRange
        get() = findFirstVisibleItemPosition()..findLastVisibleItemPosition()

    fun escaleDown(forward: Boolean): ValueAnimator {
        return getValueAnimator(forward,
                activity.resources.getInteger(R.integer.escale_down_duration).toLong(),
                AccelerateDecelerateInterpolator()) { progress ->
            for (visiblePosition in (recyclerView.layoutManager as LinearLayoutManager).visibleItensRange) {
                val holder = recyclerView.findViewHolderForAdapterPosition(visiblePosition) as ExpandCollapseViewHolder
                holder.itemView.apply {
                    container.scaleX = 1 - 0.1f * progress
                    container.scaleY = 1 - 0.02f * progress
                    foreground_view.alpha = 0.67f * progress
                    Log.d("foreground alpha", "alpha: ${holder.itemView.foreground_view.alpha}")
                    val defaultMargin = recyclerView.resources.getDimension(R.dimen.expand_collapse_holder_internal_horizontal_margin).toInt()
                    holder.internal_container.setPadding(defaultMargin, defaultMargin, defaultMargin + (64 * progress).toInt(), defaultMargin)
                }
            }
        }
    }
}
