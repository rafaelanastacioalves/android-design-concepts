package com.rafaelanastacioalves.design.concepts.common

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Color


class Utils {
    companion object {
        fun mergeColors(startColor: Int, endColor: Int, p: Float): Int {
            val red: Int = (p * Color.red(startColor) + (1 - p) * Color.red(endColor)).toInt()
            val blue: Int = (p * Color.blue(startColor) + (1 - p) * Color.blue(endColor)).toInt()
            val green: Int = (p * Color.green(startColor) + (1 - p) * Color.green(endColor)).toInt()
            val alpha: Int = (p * Color.alpha(startColor) + (1 - p) * Color.alpha(endColor)).toInt()

            return Color.argb(alpha, red, green, blue)
        }


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