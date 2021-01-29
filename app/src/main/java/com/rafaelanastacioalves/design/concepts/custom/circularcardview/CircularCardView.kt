package com.rafaelanastacioalves.design.concepts.custom.circularcardview

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import java.lang.Float.min
import kotlin.math.min

class CircularCardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    override fun setRadius(radius: Float) {
        if (radius > height/2 || radius > width/2) {
            super.setRadius(min(height, width)/2f)
        }else{
            super.setRadius(radius)
        }
    }
}