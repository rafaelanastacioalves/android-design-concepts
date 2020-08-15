package com.rafaelanastacioalves.design.concepts.ui.viewpaging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView.inflate
import com.google.android.material.tabs.TabLayoutMediator
import com.rafaelanastacioalves.design.concepts.R
import kotlinx.android.synthetic.main.viewpager_activity.*
import kotlinx.android.synthetic.main.viewpager_viewholder.view.*

class ViewPagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewpager_activity)
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = SampleAdapter()
        adapter.adapterlist = generateData()
        view_pager.adapter = adapter
        TabLayoutMediator(tablayout,view_pager) { tab, position ->
            tab.text = "Section ${position + 1}"

        }.attach()

    }

    private fun generateData(): List<String> {
        return listOf("Iem 1", "Item 2", "Item 3")
    }


}

class SampleAdapter : RecyclerView.Adapter<SampleViewHolder>() {

    lateinit var adapterlist : List<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        return SampleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.viewpager_viewholder,parent, false))
    }

    override fun getItemCount(): Int {
        return if(adapterlist.isNullOrEmpty()){
            0
        }else{
            adapterlist.size
        }
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        holder.itemView.textView.text = adapterlist.get(position)
    }

}

class SampleViewHolder(itemView: View) : ViewHolder(itemView) {

}
