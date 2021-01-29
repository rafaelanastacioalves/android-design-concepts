package com.rafaelanastacioalves.design.concepts.common

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.graphics.Color


class Utils {
    companion object {
        fun mergeColors(startColor: Int, endColor: Int, p: Float): Int {
            val red: Int = (p * Color.red(endColor) + (1 - p) * Color.red(startColor)).toInt()
            val blue: Int = (p * Color.blue(endColor) + (1 - p) * Color.blue(startColor)).toInt()
            val green: Int = (p * Color.green(endColor) + (1 - p) * Color.green(startColor)).toInt()
            val alpha: Int = (p * Color.alpha(endColor) + (1 - p) * Color.alpha(startColor)).toInt()

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