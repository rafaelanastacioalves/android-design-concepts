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
import java.text.DecimalFormat
import kotlin.math.abs
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
    private val decimalFormat: DecimalFormat
        get() {
            return DecimalFormat("#.####")
        }

    private fun fabOpeningAnimator(isForward: Boolean): ValueAnimator {
        // setup
        if (isForward && fabOriginX == 0f) {
            fabOriginX = fab.x
        }
        if (isForward && startY == 0f) {
            startY = fab.y
        }

        val finalX: Float = ((container.width / 2) - fab.width / 2).toFloat()
        val bottomMargin = 20.dp
        //TODO - Refactor: depois abstrair esse 1000L para poder ser usado pelo motion também
        val valueAnimator = getValueAnimator(isForward, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            val midlePointProgress = 0.5f
            var relativeProgress = 0f
            if (progress < midlePointProgress) {
                relativeProgress = progress / midlePointProgress
                Log.d("Fab Opening", "progress: ${relativeProgress}")
                // TODO: Refactor - esse codigo nao deveria estar instanciando tanto (05/03/2021)
                // TODO: Refactor - extrair esse 364 dp para ser a soma das alturas... (05/03/2021)
                val a: Float = (getYFromCartesianPosition(364.dp).toFloat() - startY) / ((finalX - fabOriginX).pow(2))

                fab.x = fabOriginX +
                        (relativeProgress * (finalX - fabOriginX))
                Log.d("Fab Opening", "Fab.X: ${fab.x}")

                fab.y = startY + a * ((fab.x - fabOriginX).pow(2))
                Log.d("Fab Opening", "Fab.Y: ${fab.y}")

            } else {
                if (abs(progress - midlePointProgress) < 0.001) {
                    fabMiddlePositionY = fab.y
                }
                relativeProgress = progress / midlePointProgress - 1
                fab.y = fabMiddlePositionY +
                        (container.height.toFloat() - fabMiddlePositionY - fab.height.toFloat()) * (relativeProgress)
                Log.d("Fab Vertical movement", "Fab.Y: ${decimalFormat.format(fab.y)} && " +
                        "relativeProgress: $relativeProgress &&" +
                        " container.height: ${decimalFormat.format(container.height)} " +
                        "&& fab.height: ${decimalFormat.format(fab.height)} " +
                        "&& fabMiddlePositionY: ${decimalFormat.format(fabMiddlePositionY)}")

            }
        }

        valueAnimator.doOnStart {
            if (isForward) {
                calculateFilterInitialDimensions()
            }
        }
        return valueAnimator
    }

    private fun calculateFilterInitialDimensions() {

    }

    private fun getYFromCartesianPosition(position: Int): Int {
        return (activity.screenHeight - position)
    }

}