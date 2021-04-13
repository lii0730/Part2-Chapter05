package com.example.aop_part2_chapter5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(val dataSet : ArrayList<RecyclerViewItem>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview) {
        val recyclerViewImageItem : ImageView by lazy {
            itemview.findViewById(R.id.recyclerViewImageItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerviewitem_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.recyclerViewImageItem.setImageURI(dataSet[position].imageSource)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}