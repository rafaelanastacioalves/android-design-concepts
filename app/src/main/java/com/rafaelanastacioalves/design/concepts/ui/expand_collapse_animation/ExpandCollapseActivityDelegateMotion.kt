package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnPreDraw
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.common.Utils
import kotlinx.android.synthetic.main.custom_filterlayout_motion.view.*
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.*
import kotlin.math.roundToInt

class ExpandCollapseActivityDelegateMotion(private val activity: ExpandCollapseActivity) {

    private var filterMaxWith: Int = 0
    private var filterMaxHeight: Int = 0
    private var filterFinalPosition = 0f
    private var fabMiddlePosition: Float = 0f

    private val filterLayoutMotion = activity.filterLayoutMotion
    private val expansionBackground = activity.filterLayoutMotion.expansionBackground
    private val fab = activity.filterLayoutMotion.fabMotion

    private val expandCollapseAdapter = activity.expandCollapseAdapter

    fun showFilterAnimator(isForward: Boolean): ValueAnimator {
        val valueAnimator = Utils.getValueAnimator(isForward, 1500, AccelerateDecelerateInterpolator()) { progress ->
            filterLayoutMotion.let {
                it.alpha = progress
                it.layoutParams.width = (progress * 200).roundToInt()
//            println("Width: ${filterLayout.layoutParams.width}")
                fab.alpha = 1 - progress
                fab.y = fabMiddlePosition + (activity.screenHeight - 600f - 400f) * (progress)
                it.layoutParams.height = (progress * it.withoutTabsHeight.toFloat()).toInt()
//            println("Height: ${filterLayout.layoutParams.height}")
                activity.filterLayoutMotion.requestLayout()
            }
        }
        valueAnimator.doOnStart {
            filterLayoutMotion.prepareToOpenAnimation()
            filterLayoutMotion.doOnPreDraw {
                if ((filterMaxWith + filterMaxHeight) == 0) {
                    calculateFilterFinalDimensions(it)
                    filterLayoutMotion.alpha = 0f
                }
            }
            activity.showFilter()
            filterLayoutMotion.requestLayout()
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
        if (isForward) {
            openFilterWithMotionAnimation()
        }else{
            closeFilterWithMotionAnimation()
        }
        val animateScaleDown = expandCollapseAdapter.holdersScaleDownAnimator(isForward)
        animateScaleDown.start()
    }

    private fun calculateFilterFinalDimensions(view: View) {
        if (filterMaxHeight == 0 || filterMaxWith == 0) {
            filterFinalPosition = view.y
            filterMaxWith = view.measuredWidth
            filterMaxHeight = view.height
        }
    }

    private fun fabOpeningAnimator(isForward: Boolean): ValueAnimator {
        //TODO - Refactor: depois abstrair esse 1000L para poder ser usado pelo motion também
        val valueAnimator = Utils.getValueAnimator(isForward, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            fab.translationX = -400f * progress
            fab.translationY = -400f * progress
        }
        valueAnimator.doOnEnd {
            calculateFabPosition()
        }
        return valueAnimator
    }

    private fun openFilterWithMotionAnimation() {
        // TODO: Refactor - olha quantas vezes "filterLayoutMotion.motionLayout" é chamado... (02/01/2021)
        activity.filterLayoutMotion.motionLayout.setTransition(R.id.fabOpeningStart, R.id.fabOpeningEnd)

        //TODO: Refactor - Dá pra colocar isso daqui no início?
        activity.filterLayoutMotion.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                when (p1) {
                    R.id.fabOpeningEnd -> {
                        Toast.makeText(activity, "Deu certo!", Toast.LENGTH_SHORT).show()
                        calculateFabPosition()

                        activity.filterLayoutMotion.motionLayout.setTransition(R.id.filterExpansionStart, R.id.filterExpansionEnd)
                        activity.filterLayoutMotion.motionLayout.transitionToState(R.id.filterExpansionEnd)

                    }
                    R.id.filterExpansionEnd -> {
                        activity.hideFab()
                        filterLayoutMotion.animateOpening(true)
                        activity.filterLayoutMotion.motionLayout.removeTransitionListener(this)
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })

        activity.filterLayoutMotion.motionLayout.transitionToState(R.id.fabOpeningEnd)
    }

    private fun closeFilterWithMotionAnimation() {

        filterLayoutMotion.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                when (p1) {
                    R.id.filterSettleStart -> {
                        activity.filterLayoutMotion.motionLayout.run {
                            progress = 1f
                            setTransition(R.id.filterExpansionStart, R.id.filterExpansionEnd)
                            transitionToStart()
                        }
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })
        filterLayoutMotion.animateOpening(isForward = false)
        activity.showFab()

        //TODO: Refactor - Dá pra colocar isso daqui no início?
        activity.filterLayoutMotion.motionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {

                when (p1) {
                    R.id.fabOpeningEnd -> {
                        activity.filterLayoutMotion.motionLayout.removeTransitionListener(this)
                    }
                    R.id.filterExpansionStart -> {
                        // TODO: Refactor - repetição... (27/12/2020)
                        activity.filterLayoutMotion.motionLayout.setTransition(R.id.fabOpeningStart, R.id.fabOpeningEnd)
                        activity.filterLayoutMotion.motionLayout.progress = 1f
                        activity.filterLayoutMotion.motionLayout.transitionToStart()
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })

    }

}