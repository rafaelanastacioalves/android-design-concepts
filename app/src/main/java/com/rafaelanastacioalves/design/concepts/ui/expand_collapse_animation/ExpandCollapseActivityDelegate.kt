package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnPreDraw
import com.rafaelanastacioalves.design.concepts.R
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

    private val filterLayout = activity.filterLayout
    private val fab = activity.fab
    private val container = activity.filterLayoutNormal

    private val filterViewPagerContentHeight = activity.resources.getDimension(R.dimen.viewpager_content_height).toInt()
    private val filterTabRecyclerView: Int = activity.resources.getDimension(R.dimen.custom_layout_tab_max_height).toInt()
    private val filterMaxHeightCalculated: Int = filterViewPagerContentHeight + filterTabRecyclerView
    private val expandCollapseAdapter = activity.expandCollapseAdapter

    fun showFilterAnimator(isForward: Boolean): ValueAnimator {
        val valueAnimator = getValueAnimator(isForward, 1500, AccelerateDecelerateInterpolator()) { progress ->
            filterLayout.let {
                it.alpha = progress
                it.layoutParams.width = (progress * filterMaxWith).roundToInt()
                println("Width: ${filterLayout.layoutParams.width}")
                println("filterWidth: ${filterMaxWith}")
                it.layoutParams.height = (progress * it.withoutTabsHeight.toFloat()).toInt()
                println("Height: ${filterLayout.layoutParams.height}")
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
            filterMaxWith = container.width
            filterMaxHeight = view.height
        }
    }

    var fabOriginX = 0f
    var startY = 0f
    val finalX: Float = ((container.width / 2) - fab.width / 2).toFloat()
    var quadraticPathConstant: Float = 1F
    private var fabMiddlePositionY: Float = 0f
    private var fabMiddlePositionX: Float = 0f

    private val decimalFormat: DecimalFormat = DecimalFormat("#.####")

    private fun fabOpeningAnimator(isForward: Boolean): ValueAnimator {
        // setup
        if (isForward && fabOriginX == 0f) {
            fabOriginX = fab.x
        }
        if (isForward && startY == 0f) {
            startY = fab.y
        }

        val valueAnimator = getValueAnimator(
                isForward,
                activity.resources.run {
                    getInteger(R.integer.path_duration)
                    +
                    getInteger(R.integer.expansion_duraiton)
                }.toLong(),
                AccelerateDecelerateInterpolator()
        ) { progress ->
            calculateProgress(progress)
        }

        return valueAnimator
    }

    private fun calculateProgress(progress: Float) {
        val midlePointProgress = 0.5f
        if (progress < midlePointProgress) {
            calculateArcPathProgress(progress, midlePointProgress, finalX)
        } else {
            if (abs(progress - midlePointProgress) < 0.001) calculateFabMidlePosition()
            else {
                // TODO: Refactor - Melhorar a leitura... (07/03/2021)
                calculateFabExpansion(progress, midlePointProgress)
            }
        }
    }

    private fun calculateFabMidlePosition() {
        fabMiddlePositionY = fab.y
        fabMiddlePositionX = fab.x
    }

    private fun calculateArcPathProgress(progress: Float, midlePointProgress: Float, finalX: Float) {
        var relativeProgress = progress / midlePointProgress
//        Log.d("Fab Opening", "progress: ${relativeProgress}")

        var quadraticPathConstant =
                (getYFromCartesianPosition(filterMaxHeightCalculated).toFloat() - startY) /
                        ((finalX - fabOriginX).pow(2))

        fab.x = fabOriginX + (relativeProgress * (finalX - fabOriginX))
//        Log.d("Fab Opening", "Fab.X: ${fab.x}")

        fab.y = startY + quadraticPathConstant * ((fab.x - fabOriginX).pow(2))
//        Log.d("Fab Opening", "Fab.Y: ${fab.y}")
    }

    val fabOriginalDiamater = activity.resources.getDimension(R.dimen.fab_diameter).toInt()
    private fun calculateFabExpansion(progress: Float, midlePointProgress: Float) {

        var relativeProgress = progress / midlePointProgress - 1
        fab.y = fabMiddlePositionY +
                (container.height.toFloat() - fabMiddlePositionY - fab.height.toFloat()) *
                (relativeProgress)
        fab.layoutParams.width = fabOriginalDiamater +
                ((activity.screenWidth - fabOriginalDiamater) * relativeProgress).toInt()
//        Log.d("Fab Vertical movement", "Fab.Y: ${decimalFormat.format(fab.y)} && " +
//                "relativeProgress: $relativeProgress &&" +
//                " container.height: ${decimalFormat.format(container.height)} " +
//                "&& fab.height: ${decimalFormat.format(fab.height)} " +
//                "&& fabMiddlePositionY: ${decimalFormat.format(fabMiddlePositionY)}")

        container.requestLayout()
        fab.x = fabMiddlePositionX + (0 - fabMiddlePositionX) * relativeProgress
    }

    private fun getYFromCartesianPosition(position: Int): Int {
        return (activity.screenHeight - position)
    }

}