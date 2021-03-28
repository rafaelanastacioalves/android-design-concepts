package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.common.getValueAnimator
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.*
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.pow

class ExpandCollapseActivityDelegate(private val activity: ExpandCollapseActivity) {


    private var filterMaxWidth: Int = 0
    private var filterFinalPosition = 0f

    private val filterLayout = activity.filterLayout
    private val fab: CardView = activity.fab
    private val container = activity.filterLayoutNormal

    private val filterViewPagerContentHeight = activity.resources.getDimension(R.dimen.viewpager_content_height).toInt()
    private val filterTabRecyclerView: Int = activity.resources.getDimension(R.dimen.custom_layout_tab_max_height).toInt()
    private val filterMaxHeightCalculated: Int = activity.screenWidth
    private val expandCollapseAdapter = activity.expandCollapseAdapter
    val fabOriginalDiamater = activity.resources.getDimension(R.dimen.fab_diameter).toInt()
    private val filterWidth = activity.screenWidth
    private val fabMiddlePositionY = container.height.toFloat() - (filterMaxHeightCalculated.toFloat() - fabOriginalDiamater.toFloat()) / 2
    private val fabMiddlePositionX: Float = (filterWidth - fabOriginalDiamater).toFloat() / 2


    internal fun animateFilterShowUp(isForward: Boolean) {
        val animateScaleDown = expandCollapseAdapter.holdersScaleDownAnimator(isForward)
        val animateFab = fabOpeningAnimator(isForward)
        val animateFilterExpansion = filterLayout.expansonAnimator(isForward)

        val animatorSet = AnimatorSet()
        animatorSet.play(animateFab).with(animateScaleDown)

        if (isForward) {
            animatorSet.play(animateFab).before(animateFilterExpansion)
        } else {
            animatorSet.play(animateFab).after(animateFilterExpansion)
        }
        animatorSet.start()
    }


    var fabOriginX = 0f
    var startY = 0f
    var quadraticPathConstant: Float = 0f

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
                    getInteger(R.integer.path_duration).plus(
                            getInteger(R.integer.expansion_duraiton)
                    )
                }.toLong(),
                AccelerateDecelerateInterpolator()
        ) { progress ->
            calculateProgress(progress)
        }

        valueAnimator.doOnEnd {
            // TODO: Refactor - meio bagunçado... (14/03/2021)

            if (isForward) activity.run {
                showFilter()
                hideFab()
            }
            else {
                activity.showFab()
            }
        }
        valueAnimator.doOnStart {
            if (isForward.not()) {
                activity.showFab()
                activity.hideFilter()
            }
        }


        return valueAnimator
    }


    private fun calculateProgress(progress: Float) {
        val midlePointProgress = 0.5f
        if (progress < midlePointProgress) {
            calculateArcPathProgress(progress, midlePointProgress, fabMiddlePositionX)
        } else {
            // TODO: Refactor - Melhorar a leitura... (07/03/2021)
            calculateFabExpansion(progress, midlePointProgress)
        }
    }

    private fun calculateArcPathProgress(progress: Float, midlePointProgress: Float, finalArcPathX: Float) {

        var relativeProgress = progress / midlePointProgress

        if (abs(progress - midlePointProgress) < 0.01) {
            relativeProgress = 1f
        }

        var quadraticPathConstant =
                (fabMiddlePositionY - startY) /
                        ((finalArcPathX - fabOriginX).pow(2))

        fab.x = fabOriginX + (relativeProgress * (finalArcPathX - fabOriginX))


        fab.y = startY + quadraticPathConstant * ((fab.x - fabOriginX).pow(2))
        Log.d("Fab Opening", "finalArcPathX: ${finalArcPathX} -- " + "fab.Y = ${fab.y} " +
                "fabMiddlePositionY: ${fabMiddlePositionY} " + "relativeProgress $relativeProgress")

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
            fab.layoutParams.height =
                    fabOriginalDiamater + ((filterMaxHeightCalculated - fabOriginalDiamater) *
                            expansionRelativeProgress).toInt()


            container.requestLayout()

            fab.radius = (fab.layoutParams.width / 2).toFloat()

            fab.y = fabMiddlePositionY + (container.height.toFloat() - fabMiddlePositionY - filterMaxHeightCalculated.toFloat()) * (expansionRelativeProgress)

            +(filterMaxHeightCalculated.toFloat() - fabOriginalDiamater.toFloat()) * (expansionRelativeProgress)

            fab.x = fabMiddlePositionX - ((filterWidth - fabOriginalDiamater) / 2) *
                    expansionRelativeProgress

            Log.d("Fab Vertical movement", "Fab.Y: ${decimalFormat.format(fab.y)} && " +
                    "Fab.X: ${decimalFormat.format(fab.x)} && " +
                    " fab.width: ${decimalFormat.format(fab.width)} " +
                    "&& fab.height: ${decimalFormat.format(fab.height)} " +
                    "expansionRelativeProgress: $expansionRelativeProgress &&" +
                    " container.height: ${decimalFormat.format(container.height)} " +
                    "&& fabMiddlePositionY: ${decimalFormat.format(fabMiddlePositionY)} " +
                    "Fab.visibility = ${fab.visibility.toString()}")

        }
        if (relativeProgress > 0.8f) {
            // TODO: Refactor - instanciacao... nomes.... (10/03/2021)
            var radiusChangeRelativeProgress = (relativeProgress - 0.8f) / 0.2f
            fab.radius = filterWidth / 2 +
                    (0 - filterWidth / 2) * radiusChangeRelativeProgress
        }
    }

    private fun getYFromCartesianPosition(position: Int): Int {
        return (activity.screenHeight - position)
    }

}