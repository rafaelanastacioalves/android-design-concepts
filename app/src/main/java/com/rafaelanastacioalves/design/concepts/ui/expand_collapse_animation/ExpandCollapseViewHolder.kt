package com.rafaelanastacioalves.design.concepts.ui.expand_collapse_animation;

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

import com.rafaelanastacioalves.design.concepts.domain.entities.FakeData
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.expand_collapse_viewholder.view.*

class ExpandCollapseViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), View.OnClickListener, LayoutContainer{


    init {
        itemView.container.setOnClickListener(this)
    }

    override fun onClick(v: View) {
//        aRecyclerViewListener.onClick(v, getAdapterPosition());
    }

    fun bind(aMainEntity: FakeData, context: Context) {

//        itemView.entity_detail_title_textview.setText(aMainEntity.title);
//        val placeholderList: StateListDrawable= context.getResources().getDrawable(R.drawable.ic_placeholder_map_selector) as StateListDrawable;
//        Picasso.get()
//                .load(aMainEntity.imageUrl)
//                .placeholder(placeholderList)
//                .into(itemView.main_entity_imageview as ImageView);


    }
}
