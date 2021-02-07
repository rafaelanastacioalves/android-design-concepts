package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnPreDraw
import com.rafaelanastacioalves.design.concepts.common.Utils
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.*
import kotlin.math.roundToInt

class ExpandCollapseActivityDelegate(private val activity: ExpandCollapseActivity) {

    private var filterMaxWith: Int = 0
    private var filterMaxHeight: Int = 0
    private var filterFinalPosition = 0f
    private var fabMiddlePosition: Float = 0f

    private val filterLayout = activity.filterLayout
    private val fab = activity.fab

    private val expandCollapseAdapter = activity.expandCollapseAdapter

    fun showFilterAnimator(isForward: Boolean): ValueAnimator {
        val valueAnimator = Utils.getValueAnimator(isForward, 1500, AccelerateDecelerateInterpolator()) { progress ->
            filterLayout.let {
                it.alpha = progress
                it.layoutParams.width = (progress * filterMaxWith).roundToInt()
//            println("Width: ${filterLayout.layoutParams.width}")
                fab.alpha = 1 - progress
                fab.y = fabMiddlePosition + (activity.screenHeight - fabMiddlePosition - 400f) * (progress)
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
        fabMiddlePosition = fab.y
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

    private fun fabOpeningAnimator(isForward: Boolean): ValueAnimator {
        val startX = fab.x
        val finalX = activity.screenWidth / 2
        //TODO - Refactor: depois abstrair esse 1000L para poder ser usado pelo motion também
        val valueAnimator = Utils.getValueAnimator(isForward, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            fab.x = startX + progress * (finalX - startX)
            fab.translationY = -400f * progress
        }
        valueAnimator.doOnEnd {
            calculateFabPosition()
        }
        return valueAnimator
    }

}