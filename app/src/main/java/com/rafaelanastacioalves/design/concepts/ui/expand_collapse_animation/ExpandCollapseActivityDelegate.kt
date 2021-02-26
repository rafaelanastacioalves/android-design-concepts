package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnPreDraw
import com.rafaelanastacioalves.design.concepts.common.dp
import com.rafaelanastacioalves.design.concepts.common.getValueAnimator
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.*
import kotlin.math.pow
import kotlin.math.roundToInt

class ExpandCollapseActivityDelegate(private val activity: ExpandCollapseActivity) {

    private var filterMaxWith: Int = 0
    private var filterMaxHeight: Int = 0
    private var filterFinalPosition = 0f
    private var fabMiddlePositionY: Float = 0f

    private val filterLayout = activity.filterLayout
    private val fab = activity.fab
    private val container = activity.filterLayoutNormal

    private val expandCollapseAdapter = activity.expandCollapseAdapter

    fun showFilterAnimator(isForward: Boolean): ValueAnimator {
        val valueAnimator = getValueAnimator(isForward, 1500, AccelerateDecelerateInterpolator()) { progress ->
            filterLayout.let {
                it.alpha = progress
                it.layoutParams.width = (progress * filterMaxWith).roundToInt()
//            println("Width: ${filterLayout.layoutParams.width}")
                it.layoutParams.height = (progress * it.withoutTabsHeight.toFloat()).toInt()
//            println("Height: ${filterLayout.layoutParams.height}")
                it.requestLayout()
            }
        }
        valueAnimator.doOnStart {
            filterLayout.prepareToOpenAnimation()
            filterLayout.doOnPreDraw {
                if ((filterMaxWith + filterMaxHeight) == 0) {
                    calculateFilterFinalDimensions(it)
                    filterLayout.alpha = 0f
                }
            }
            activity.showFilter()
            if (isForward.not()) {
                activity.showFab()
            }
        }
        valueAnimator.doOnEnd {
            if (isForward) activity.hideFab() else {
                activity.showFab()
            }
        }
        return valueAnimator
    }

    private fun calculateFabPosition() {
        fabMiddlePositionY = fab.y
    }

    internal fun animateFilterShowUp(isForward: Boolean) {
        val animateScaleDown = expandCollapseAdapter.holdersScaleDownAnimator(isForward)
        val animateFab = fabOpeningAnimator(isForward)
        val animateShowFilter = showFilterAnimator(isForward)
        val animateFilterExpansion = filterLayout.expansonAnimator(isForward)

        val animatorSet = AnimatorSet()
        animatorSet.play(animateFab).with(animateScaleDown)

        if (isForward) {
            animatorSet.play(animateFab).before(animateShowFilter)
            animatorSet.play(animateShowFilter).before(animateFilterExpansion)
        } else {
            animatorSet.play(animateShowFilter).after(animateFilterExpansion)
            animatorSet.play(animateFab).after(animateShowFilter)
        }
        animatorSet.start()
    }

    private fun calculateFilterFinalDimensions(view: View) {
        if (filterMaxHeight == 0 || filterMaxWith == 0) {
            filterFinalPosition = view.y
            filterMaxWith = view.measuredWidth
            filterMaxHeight = view.height
        }
    }

    var fabOriginX = 0f
    var startY = 0f
    private fun fabOpeningAnimator(isForward: Boolean): ValueAnimator {
        // setup
        if (isForward && fabOriginX == 0f) {
            fabOriginX = fab.x
        }
        if (isForward && startY == 0f) {
            startY = fab.y
        }

        val finalX: Float = (activity.screenWidth / 2).toFloat()
        val bottomMargin = 20.dp
        //TODO - Refactor: depois abstrair esse 1000L para poder ser usado pelo motion também
        val valueAnimator = getValueAnimator(isForward, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            val midlePointProgress = 0.5f
            var relativeProgress = 0f
            if (progress < midlePointProgress) {
                relativeProgress = progress / midlePointProgress
                Log.d("Fab Opening", "progress: ${relativeProgress}")
                fab.x = fabOriginX +
                        (relativeProgress * (finalX - fabOriginX))
                Log.d("Fab Opening", "Fab.X: ${fab.x}")
                fab.y = startY - 0.02F * ((fab.x - fabOriginX).pow(2))
                Log.d("Fab Opening", "Fab.Y: ${fab.y}")
            } else {
                if (progress == midlePointProgress) {
                    fabMiddlePositionY = fab.y
                }
                relativeProgress = progress / midlePointProgress - 1
                fab.y = fabMiddlePositionY +
                        (container.height - fabMiddlePositionY - fab.height) * (relativeProgress)
            }
        }
        valueAnimator.doOnEnd {
            calculateFabPosition()
        }
        return valueAnimator
    }

    private fun getYFromCartesianPosition(position: Int): Int {
        return (activity.screenHeight - position)
    }

}