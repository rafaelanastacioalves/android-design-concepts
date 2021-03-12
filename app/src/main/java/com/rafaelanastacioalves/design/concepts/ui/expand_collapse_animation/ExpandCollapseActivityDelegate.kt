package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnPreDraw
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.common.dp
import com.rafaelanastacioalves.design.concepts.common.getValueAnimator
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.*
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.view.*
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

class ExpandCollapseActivityDelegate(private val activity: ExpandCollapseActivity) {


    private var filterMaxWidth: Int = 0
    private var filterMaxHeight: Int = 0
    private var filterFinalPosition = 0f

    private val filterLayout = activity.filterLayout
    private val fab: CardView = activity.fab
    private val container = activity.filterLayoutNormal

    private val filterViewPagerContentHeight = activity.resources.getDimension(R.dimen.viewpager_content_height).toInt()
    private val filterTabRecyclerView: Int = activity.resources.getDimension(R.dimen.custom_layout_tab_max_height).toInt()
    private val filterMaxHeightCalculated: Int = filterViewPagerContentHeight + filterTabRecyclerView
    private val expandCollapseAdapter = activity.expandCollapseAdapter

    fun showFilterAnimator(isForward: Boolean): ValueAnimator {
        val valueAnimator = getValueAnimator(isForward, 1500, AccelerateDecelerateInterpolator()) { progress ->
            filterLayout.let {
                it.alpha = progress
                it.layoutParams.width = (progress * filterMaxWidth).roundToInt()
                println("Width: ${filterLayout.layoutParams.width}")
                println("filterWidth: ${filterMaxWidth}")
                it.layoutParams.height = (progress * it.withoutTabsHeight.toFloat()).toInt()
                println("Height: ${filterLayout.layoutParams.height}")
                it.requestLayout()
            }
        }
        valueAnimator.doOnStart {
            filterLayout.prepareToOpenAnimation()
            filterLayout.doOnPreDraw {
                if ((filterMaxWidth + filterMaxHeight) == 0) {
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
        if (filterMaxHeight == 0 || filterMaxWidth == 0) {
            filterFinalPosition = view.y
            filterMaxWidth = container.width
            filterMaxHeight = view.height
        }
    }

    private val filterWidth = activity.screenWidth
    val fabOriginalDiamater = activity.resources.getDimension(R.dimen.fab_diameter).toInt()
    var fabOriginX = 0f
    var startY = 0f
    var quadraticPathConstant: Float = 1F
    private var fabMiddlePositionY: Float = 0f
    private val fabMiddlePositionX: Float = (filterWidth - fabOriginalDiamater).toFloat() / 2
    private var fabInternalIconMidlePositionY: Float = 0f

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
        val midlePointProgress = 0.4f
        if (progress < midlePointProgress) {
            calculateArcPathProgress(progress, midlePointProgress, fabMiddlePositionX)
        } else {
            if (abs(progress - midlePointProgress) < 0.0000001) calculateFabMidlePosition()
            else {
                // TODO: Refactor - Melhorar a leitura... (07/03/2021)
                calculateFabExpansion(progress, midlePointProgress)
            }
        }
    }

    private fun calculateFabMidlePosition() {
        fabMiddlePositionY = fab.y
        fabInternalIconMidlePositionY = fab.fabInternalIcon.y
    }

    private fun calculateArcPathProgress(progress: Float, midlePointProgress: Float, finalArcPathX: Float) {
        var relativeProgress = progress / midlePointProgress
//        Log.d("Fab Opening", "progress: ${relativeProgress}")

        var quadraticPathConstant =
                (getYFromCartesianPosition(filterMaxHeightCalculated).toFloat() - startY) /
                        ((finalArcPathX - fabOriginX).pow(2))

        fab.x = fabOriginX + (relativeProgress * (finalArcPathX - fabOriginX))
        Log.d("Fab Opening", "finalArcPathX: ${finalArcPathX} -- " +
                "relativeProgress $relativeProgress")

        fab.y = startY + quadraticPathConstant * ((fab.x - fabOriginX).pow(2))
//        Log.d("Fab Opening", "Fab.Y: ${fab.y}")
    }

    private fun calculateFabExpansion(progress: Float, midlePointProgress: Float) {
        // TODO: Refactor - instanciacao... (10/03/2021)

        var relativeProgress = (progress - midlePointProgress) / (1 - midlePointProgress)
        // TODO: Refactor - extrair o 0.8f (12/03/2021)
        if (0 < (0.8f - relativeProgress) && (0.8f - relativeProgress) < 0.01) {
            relativeProgress = 0.8f
        }

        if (relativeProgress <= 0.8f) {
            var expansionRelativeProgress = relativeProgress / 0.8f
            fab.layoutParams.width = fabOriginalDiamater +
                    ((filterWidth - fabOriginalDiamater) * expansionRelativeProgress).toInt()
            fab.layoutParams.height = fabOriginalDiamater + ((filterMaxHeightCalculated - fabOriginalDiamater) * expansionRelativeProgress).toInt()
            fab.x = fabMiddlePositionX - ((filterWidth - fabOriginalDiamater) / 2) * expansionRelativeProgress

            Log.d("Fab Vertical movement", "Fab.Y: ${decimalFormat.format(fab.y)} && " +
                    "Fab.X: ${decimalFormat.format(fab.x)} && " +
                    " fab.width: ${decimalFormat.format(fab.width)} " +
                    "&& fab.height: ${decimalFormat.format(fab.height)} " +
                    "expansionRelativeProgress: $expansionRelativeProgress &&" +
                    " container.height: ${decimalFormat.format(container.height)} " +
                    "&& fabMiddlePositionY: ${decimalFormat.format(fabMiddlePositionY)}")

            container.requestLayout()
            fab.y = fabMiddlePositionY +
                    (container.height.toFloat() - fabMiddlePositionY - fab.height.toFloat()) *
                    (expansionRelativeProgress)
            fab.fabInternalIcon.y = 0 +
                    (fab.height.toFloat() -
                            0 -
                            fab.fabInternalIcon.height.toFloat() -
                            16.dp
                            ) * (expansionRelativeProgress)
        }
        if (relativeProgress >= 0.8f) {
            // TODO: Refactor - instanciacao... nomes.... (10/03/2021)
            var radiusChangeRelativeProgress = (relativeProgress - 0.8f) / 0.2f
            fab.radius = fabOriginalDiamater / 2 +
                    (0 - fabOriginalDiamater / 2) * radiusChangeRelativeProgress
        }
    }

    private fun getYFromCartesianPosition(position: Int): Int {
        return (activity.screenHeight - position)
    }

}