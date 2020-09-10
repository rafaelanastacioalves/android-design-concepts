package com.rafaelanastacioalves.design.concepts.custom.filterlayout

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import kotlinx.android.synthetic.main.custom_filterlayout.view.*
import kotlinx.android.synthetic.main.custom_filterlayout_viewpager_item_viewholder.view.*

class FilterLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var delegate: FilterLayoutContract
    val list = generateTabItemViewHolderData()
    lateinit var tabForViewPagerAdapter: TabForViewPagerAdapter
    lateinit var viewPagerAdapter: ViewPagerAdapter
    var totalDxTabScroll: Int = 0

    init{
        inflate(context, R.layout.custom_filterlayout, this)
        setupViewPager()
        setupTabForRecyclerView()
    }

    public fun init(delegate: FilterLayoutContract): Unit {
        this.delegate = delegate
        setupListeners()
    }

    private fun setupListeners() {
        okButton.setOnClickListener{
            delegate.onFilterDismiss()
        }

        dismissButton.setOnClickListener{
            delegate.onFilterDismiss()
        }
    }

    private fun setupTabForRecyclerView() {
        tabForViewPagerAdapter = TabForViewPagerAdapter(object : RecyclerViewClickListener {
            override fun onClick(view: View, position: Int) {
                view_pager.setCurrentItem(position, true)
            }
        })
        tabForViewPagerAdapter.customFilterLayoutTabList = list
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.HORIZONTAL
        viewpager_tab_recyclerview.apply {
            adapter = tabForViewPagerAdapter
            layoutManager = linearLayoutManager
            suppressLayout(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    totalDxTabScroll += dx
                }
            })
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(object : RecyclerViewClickListener{
            override fun onClick(view: View, position: Int) {
                list.get(position).hasSelectinos = list.get(position).hasSelectinos.not()
                updateAdapters()
            }

        })
        viewPagerAdapter.adapterlist = list
        view_pager.adapter = viewPagerAdapter
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            var currentPosition = 0
            var currentAbsoluteOffset = 0
            var currentOffSet: Float = 0F

            private fun calculateScrollBy(offsetPercent: Float, position: Int): Int {
                var dx = (position + offsetPercent) * (tabForViewPagerAdapter.viewHolderWidth) - totalDxTabScroll
                println("Calculate ScrollBy dx: ${dx} ")
                return dx.toInt()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                println("onPageScrolled: position: ${position}, positionOffSet: ${positionOffset}, positionOffSetPixels ${positionOffsetPixels}")
                viewpager_tab_recyclerview.let {
                    it.animateToPosition(position, calculateScrollBy(positionOffset, position))
                    if (position > currentPosition || positionOffset == 0.0F) {
                        currentPosition = position
                        currentOffSet = 0F
                        tabForViewPagerAdapter.selectedItemIndex = position
                        it.suppressLayout(false)
                        tabForViewPagerAdapter.notifyDataSetChanged()
                        it.suppressLayout(true)
                    }
                }


            }
        })
    }

    private fun updateAdapters() {
        tabForViewPagerAdapter.customFilterLayoutTabList = list
        viewpager_tab_recyclerview.suppressLayout(false)
        tabForViewPagerAdapter.notifyDataSetChanged()
        viewpager_tab_recyclerview.suppressLayout(true)
        viewPagerAdapter.adapterlist = list
        viewPagerAdapter.notifyDataSetChanged()

    }

    private fun generateTabItemViewHolderData(): List<CustomFilterLayoutTabItemElement> {
        val arrayList = ArrayList<CustomFilterLayoutTabItemElement>()
        for (i in 1..6) {
            arrayList.add(CustomFilterLayoutTabItemElement("Tab ${i}", "Visualization ${i}", false))
        }
        return arrayList
    }

    private fun RecyclerView.animateToPosition(position: Int, dx: Int) {

        suppressLayout(false)
        scrollBy(dx, 0)
        suppressLayout(true)
    }


}

class ViewPagerAdapter(val recyclerViewClickListener: RecyclerViewClickListener) : RecyclerView.Adapter<SampleViewHolder>() {
    lateinit var adapterlist: List<CustomFilterLayoutTabItemElement>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_filterlayout_viewpager_item_viewholder, parent, false))
    }

    override fun getItemCount(): Int {
        return if (adapterlist.isNullOrEmpty()) {
            0
        }else{
            adapterlist.size
        }
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.itemView.textView.text = adapterlist.get(position).textPage
        holder.itemView.button.setOnClickListener { v ->
            recyclerViewClickListener.onClick(
                    v, position
            )
        }
    }
}

class SampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

interface FilterLayoutContract {
    fun onFilterDismiss()
    fun onFilterConfirmed()
}

