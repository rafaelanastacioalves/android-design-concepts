package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.domain.entities.CustomFilterLayoutTabItemElement
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.custom_filterlayout_viewpager_item_holder_1.*

class ViewPagerAdapter(private val customFilter : FilterLayout) : RecyclerView.Adapter<ViewPagerViewHolder>() {
    private val itemsSelectionMap  = mutableMapOf<Int, MutableList<Int>>()
    lateinit var adapterlist: List<CustomFilterLayoutTabItemElement>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        return if (viewType == 0) {
            ViewPagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_filterlayout_viewpager_item_holder_3, parent, false))
        } else {
            ViewPagerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_filterlayout_viewpager_item_holder_1, parent, false))
        }

    }

    override fun getItemCount(): Int {
        return if (adapterlist.isNullOrEmpty()) {
            0
        } else {
            adapterlist.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, holderPosition: Int) {
        val holderItemIndexSelectionList =  itemsSelectionMap.getOrPut(holderPosition, {mutableListOf()})
        holder.listOfFilterItems.forEachIndexed { holderItemIndex, view ->
            view.setOnClickListener{
                if (holderItemIndexSelectionList.contains(holderItemIndex)){
                    holderItemIndexSelectionList.removeAt(holderItemIndexSelectionList.indexOf(holderItemIndex))
                    animateItemSelection(holderItemIndex, false)
                }else{
                    holderItemIndexSelectionList.add(holderItemIndex)
                    animateItemSelection(holderItemIndex, true)
                }
                customFilter.onFilterItemClicked(holderPosition, itemsSelectionMap )
            }
        }
    }

    private fun animateItemSelection(index: Int, isSelection: Boolean) {
            Log.d(javaClass.name, "Animating item selection of index: $index - $isSelection")
        }
}

class ViewPagerViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    var listOfFilterItems : List<View>

    init {
        listOfFilterItems = listOfNotNull(filterOne, filterTwo, filterThree, filterFour, filterFive, filterSix)
    }
}