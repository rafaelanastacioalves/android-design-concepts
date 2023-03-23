package com.rafaelanastacioalves.design.concepts.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.rafaelanastacioalves.design.concepts.R
import com.rafaelanastacioalves.design.concepts.listeners.RecyclerViewClickListener
import com.rafaelanastacioalves.design.concepts.ui.articledetail.ArticleDetailActivity
import com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation.ExpandCollapseActivity
import com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation.ExpandCollapseActivityWithMotion
import com.rafaelanastacioalves.design.concepts.ui.motion_layout_scroll.ScrollWithMotion

class HomeActivity : AppCompatActivity(), RecyclerViewClickListener {
    private val recyclerView : RecyclerView by lazy {
        findViewById(R.id.recycler_view) as RecyclerView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        setupToolbar()
        setupRecyclerView()
        animateIntro()

    }

    private fun goTo(intent: Intent) {
        startActivity(intent)
    }


    override fun onPostResume() {
        super.onPostResume()

        val handler = Handler()
        handler.postDelayed({
            (recyclerView.adapter as Adapter).setLIst(generateData())
        }, 300)


    }

    private fun setupToolbar() {
        title = "Design Demonstrations"
        setSupportActionBar(findViewById(R.id.toolbar))
        actionBar?.setDisplayShowTitleEnabled(true)
    }


    private fun setupRecyclerView() {
        val adapter = Adapter(this)
        adapter.setHasStableIds(true)

        recyclerView.adapter = adapter
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.requestSimpleAnimationsInNextLayout()
        recyclerView.layoutManager = staggeredGridLayoutManager
        (recyclerView.adapter as Adapter).notifyDataSetChanged()
    }

    private fun animateIntro() {
        recyclerView.itemAnimator = AnimationIntroAnimator()
    }

    override fun onClick(view: View, position: Int) {
        val intent = (recyclerView.adapter as Adapter).mArraylist!!.get(position).Intent
        startActivity(intent)
    }

    private fun generateData(): List<HomeItemData> {
        val list = ArrayList<HomeItemData>()
        list.add(HomeItemData("Advanced Animation Demonstration", "* A Filter with animated interactions (motion layout)\n" +
                "* Expansion/collapse animation effect ", Intent(this, ExpandCollapseActivity::class.java)))
        list.add(HomeItemData("Scroll With MotionLayot", "Multiple Animated effect while scrolling", Intent(this, ScrollWithMotion::class.java)))
        list.add(HomeItemData("Parallax Demonstration", "A simple scroll with parallax effect", Intent(this, ArticleDetailActivity::class.java)))
        return list
    }

    private class Adapter : RecyclerView.Adapter<ArticleItemViewHolder?> {
        private lateinit var clickListener: RecyclerViewClickListener

        var mArraylist: List<HomeItemData>? = null
        constructor( clickListener: RecyclerViewClickListener) {
            this.clickListener = clickListener
        }

        fun setLIst(list : List<HomeItemData>) {
            this.mArraylist = list
            notifyDataSetChanged()
        }

       override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleItemViewHolder {
            Log.i("adapter", "onCreateViewHolder")
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.home_artcile_viewholder, parent, false)
            val vh = ArticleItemViewHolder(view)
            return vh
        }


        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun onBindViewHolder(holder: ArticleItemViewHolder, position: Int) {
            val data = mArraylist!!.get(position)
            holder.titleView.text = data.title
            holder.subtitleView.text = data.subtitle
            holder.itemView.setOnClickListener { clickListener.onClick(view = it, position = position ) }
        }

        override fun getItemCount(): Int {
            return if (mArraylist != null) {
                mArraylist!!.size
            } else {
                0
            }
        }
    }




    class ArticleItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var titleView: TextView
        var subtitleView: TextView
        init {
            titleView = view.findViewById<View>(R.id.article_title) as TextView
            subtitleView = view.findViewById<View>(R.id.article_subtitle) as TextView
        }

    }
}
