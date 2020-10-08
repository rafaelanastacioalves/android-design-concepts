package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.common.Utils
import kotlinx.android.synthetic.main.custom_filterlayout.view.*
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class FilterLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tabMaxHeight by lazy { resources.getDimension(R.dimen.customLayoutTabMaxHeight) }
    var withoutTabsHeight: Int = 0
    private lateinit var delegate: FilterLayoutContract
    private var customFilterLayoutHandler : CustomFilterLayoutHandler

    init {
        inflate(context, R.layout.custom_filterlayout, this)
        customFilterLayoutHandler = CustomFilterLayoutHandler(button_background, viewpagerTabRecyclerview, viewPager)
        calculateTabDimensions()
    }

    fun setFilterLayoutCallbacksListeners(delegate: FilterLayoutContract) {
        this.delegate = delegate
        setupListeners()
    }

    private fun setupListeners() {
        okButton.setOnClickListener {
            delegate.onFilterDismiss()
        }

        dismissButton.setOnClickListener {
            delegate.onFilterDismiss()
        }
    }


    fun prepareToOpenAnimation() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(buttonsContainer)
        okButton.isVisible = false
        constraintSet.clear(okButton.id, ConstraintSet.START)
        constraintSet.clear(okButton.id, ConstraintSet.END)
        okButton.x = this.width / 2f
        dismissButton.isVisible = false
        constraintSet.clear(dismissButton.id, ConstraintSet.START)
        constraintSet.clear(dismissButton.id, ConstraintSet.END)
        dismissButton.x = this.width / 2f
        constraintSet.applyTo(buttonsContainer)
    }

    private fun animateOpening(progress: Float) {
        okButton.isVisible = true
        okButton.alpha = progress
        dismissButton.isVisible = true
        dismissButton.alpha = progress
        okButton.x = width / 2 + (width / 4) * (progress)
        dismissButton.x = width / 2 - (width / 4) * (progress)
        viewpagerTabRecyclerview.layoutParams.height = (tabMaxHeight * progress).toInt()
        layoutParams.height = withoutTabsHeight + (tabMaxHeight * progress).roundToInt()
//        println("With Tab Height: ${layoutParams.height}")

        requestLayout()
    }

    private fun calculateTabDimensions() {
        isVisible = true
        doOnLayout {
            withoutTabsHeight = it.measuredHeight
            isVisible = false
        }
    }

    fun expansonAnimator(isForward: Boolean): ValueAnimator {
        return Utils.getValueAnimator(isForward, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            animateOpening(progress)
        }
    }

}

interface ViewPagerFilterItemsContract {
    fun onFilterItemClicked(pagePosition: Int, viewPagerItemsSelectionMap: Map<Int, List<Int>>)
}

interface FilterLayoutContract {
    fun onFilterDismiss()
    fun onFilterConfirmed()
}

