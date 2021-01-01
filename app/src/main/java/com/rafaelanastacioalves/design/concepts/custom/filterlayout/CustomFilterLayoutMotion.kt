package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import com.rafaelanastacioalves.design.concepts.R
import kotlinx.android.synthetic.main.custom_filterlayout.view.*
import kotlinx.android.synthetic.main.custom_filterlayout.view.button_background
import kotlinx.android.synthetic.main.custom_filterlayout.view.dismissButton
import kotlinx.android.synthetic.main.custom_filterlayout.view.okButton
import kotlinx.android.synthetic.main.custom_filterlayout.view.viewPager
import kotlinx.android.synthetic.main.custom_filterlayout.view.viewpagerTabRecyclerview
import kotlinx.android.synthetic.main.custom_filterlayout_motion.view.*

@Suppress("DEPRECATION")
class FilterLayoutMotion @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {


    //TODO: Refactor - cuidado com todo o código repetido... vamos colocar numa classe pai....
    private val tabMaxHeight by lazy { resources.getDimension(R.dimen.customLayoutTabMaxHeight) }
    var withoutTabsHeight: Int = 0
    private lateinit var delegate: FilterLayoutContract
    private var customFilterLayoutHandler: CustomFilterLayoutHandler

    init {
        gravity = Gravity.BOTTOM
        orientation = VERTICAL
        //TODO: Refactor - esses métodos poderiam estar encapsulados... no mínimo
//        background = resources.getDrawable(R.color.DarkGreen)
        inflate(context, R.layout.custom_filterlayout_motion, this)
        customFilterLayoutHandler = CustomFilterLayoutHandler(button_background, viewpagerTabRecyclerview, viewPager)
//        calculateTabDimensions()
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


    fun animateOpening(isForward: Boolean) {
        motionLayout.setTransition(R.id.filterOpeningStart, R.id.filterOpeningEnd)
        if (isForward) {
            motionLayout.transitionToState(R.id.filterOpeningEnd)
        } else {
            motionLayout.run {
                progress = 1f
                transitionToStart()
            }


        }
    }

    private fun calculateTabDimensions() {
        isVisible = true
        doOnLayout {
            withoutTabsHeight = it.measuredHeight
            isVisible = false
        }
    }
}

