package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation

import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import com.rafaelanastacioalves.design.concepts.R
import kotlinx.android.synthetic.main.expand_collapse_animation_activity.*

class ExpandCollapseActivityDelegateMotion(private val activity: ExpandCollapseActivity) {


    private var fabMiddlePosition: Float = 0f

    private val filterLayoutMotion = activity.filterLayoutMotion

    private val expandCollapseAdapter = activity.expandCollapseAdapter


    internal fun animateFilterShowUp(isForward: Boolean) {
        if (isForward) {
            openFilterWithMotionAnimation()
        }else{
            closeFilterWithMotionAnimation()
        }
        val animateScaleDown = expandCollapseAdapter.holdersScaleDownAnimator(isForward)
        animateScaleDown.start()
    }



    private fun openFilterWithMotionAnimation() {
        // TODO: Refactor - olha quantas vezes "filterLayoutMotion.motionLayout" é chamado... (02/01/2021)
        activity.filterLayoutMotion.setTransition(R.id.base, R.id.fabPath)

        //TODO: Refactor - Dá pra colocar isso daqui no início?
        activity.filterLayoutMotion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                when (p1) {
                    R.id.fabPath -> {
                        Toast.makeText(activity, "Deu certo!", Toast.LENGTH_SHORT).show()

                        activity.filterLayoutMotion.transitionToState(R.id.filterExpansion)

                    }
                    R.id.filterExpansion -> {

                        filterLayoutMotion.animateSettle(true)
                    }

                    R.id.filterSettle -> {
                        filterLayoutMotion.removeTransitionListener(this)
                        activity.setupFabMotionExpanded()
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })

        activity.filterLayoutMotion.transitionToState(R.id.fabPath)
    }

    private fun closeFilterWithMotionAnimation() {
        filterLayoutMotion.setTransition(R.id.filterExpansion, R.id.filterSettle)

        filterLayoutMotion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                when (p1) {
                    R.id.filterExpansion -> {
                        activity.filterLayoutMotion.run {
                            progress = 1f
                            setTransition(R.id.fabPath, R.id.filterExpansion)
                            transitionToStart()
                        }
                    }
                    R.id.fabPath -> {
                        activity.filterLayoutMotion.run {
                            progress = 1f
                            setTransition(R.id.base, R.id.fabPath)
                            transitionToStart()
                        }
                        filterLayoutMotion.removeTransitionListener(this)
                        activity.setupFabMotionCollapsed()
                    }

                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })
        filterLayoutMotion.animateSettle(isForward = false)
    }

}