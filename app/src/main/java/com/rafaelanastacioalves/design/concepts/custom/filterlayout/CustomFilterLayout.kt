package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.common.getValueAnimator
import kotlinx.android.synthetic.main.custom_filterlayout.view.*
import kotlin.math.roundToInt

@Suppress("DEPRECATION")
class FilterLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    //TODO: O maxheight vai ser usado tanto aqui quanto em XML do motion layout... melhor unificar
    private val tabMaxHeight by lazy { resources.getDimension(R.dimen.custom_layout_tab_max_height) }
    var withoutTabsHeight: Int = 0
    private lateinit var delegate: FilterLayoutContract
    private var customFilterLayoutHandler: CustomFilterLayoutHandler

    init {

        //TODO: Refactor - esses métodos poderiam estar encapsulados... no mínimo

        background = resources.getDrawable(R.color.colorPrimaryDark)
        inflate(context, R.layout.custom_filterlayout, this)
        customFilterLayoutHandler = CustomFilterLayoutHandler(button_background, tabRecyclerview, viewPager)
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


    private fun calculateTabDimensions() {
        isVisible = true
        doOnLayout {
            withoutTabsHeight = it.width
            isVisible = false
        }
    }

    fun setlleAnimator(isForward: Boolean): ValueAnimator {
        val animator = getValueAnimator(isForward, 1000L, AccelerateDecelerateInterpolator()) { progress ->
            animateOpening(progress)
        }
        animator.doOnStart {
            if (isForward) {
                tabRecyclerview.isVisible = true
                okButton.isVisible = true
                dismissButton.isVisible = true
            }
        }
        return animator
    }

    private fun animateOpening(progress: Float) {

        dismissButton.alpha = progress
        okButton.x = width / 2 - okButton.width / 2 + (width / 4) * (progress)
        viewPager.alpha = progress
        dismissButton.x = width / 2 - (width / 4) * (progress)
        tabRecyclerview.layoutParams.height = (tabMaxHeight * progress).toInt()
        tabRecyclerview.alpha = progress
        layoutParams.height = withoutTabsHeight + (tabMaxHeight * progress).roundToInt()
//        println("With Tab Height: ${layoutParams.height}")

        requestLayout()
    }

}

interface ViewPagerFilterItemsContract {
    fun onFilterItemClicked(pagePosition: Int, viewPagerItemsSelectionMap: Map<Int, List<Int>>)
}

interface FilterLayoutContract {
    fun onFilterDismiss()
    fun onFilterConfirmed()
}

