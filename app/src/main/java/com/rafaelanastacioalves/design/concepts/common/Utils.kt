package com.rafaelanastacioalves.design.concepts.common

import android.graphics.Color


class Utils {
    companion object {
        fun mergeColors(startColor: Int, endColor: Int, p: Float): Int {
            val red : Int = (p*Color.red(startColor) + (1-p)*Color.red(endColor)).toInt()
            val blue : Int = (p*Color.blue(startColor) + (1-p)*Color.blue(endColor)).toInt()
            val green : Int = (p*Color.green(startColor) + (1-p)*Color.green(endColor)).toInt()
            val alpha : Int = (p*Color.alpha(startColor) + (1-p)*Color.alpha(endColor)).toInt()

            return Color.argb(alpha,red,green,blue)
        }
    }
}