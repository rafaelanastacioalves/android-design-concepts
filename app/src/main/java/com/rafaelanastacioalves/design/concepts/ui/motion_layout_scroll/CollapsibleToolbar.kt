package com.rafaelanastacioalves.design.concepts.ui.motion_layout_scroll

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.appbar.AppBarLayout

class CollapsibleToolbar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MotionLayout(context, attrs, defStyleAttr), AppBarLayout.OnOffsetChangedListener {
    override fun onOffsetChanged(appBarLayout: AppBarLayout?, offset: Int) {
        progress = -offset / appBarLayout?.totalScrollRange?.toFloat()!!
        Log.d(javaClass.simpleName, "Progress: ${progress}  offset: ${offset}")

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (parent as AppBarLayout)?.addOnOffsetChangedListener(this)
    }

}