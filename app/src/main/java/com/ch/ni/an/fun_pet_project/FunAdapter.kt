package com.ch.ni.an.fun_pet_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class FunAdapter: ListAdapter<AnyItem, FunAdapter.FunHolder>(FunDiffUtil()) {
    class FunHolder(
        view: View
    ): RecyclerView.ViewHolder(view) {

        fun bind(item: AnyItem){
            val title = itemView.findViewById<TextView>(R.id.titleTextView)
            val content = itemView.findViewById<TextView>(R.id.contentTextView)
            title.text = item.title
            content.text = item.content
        }

    }



    override fun onCreateViewHolder(parent :ViewGroup, viewType :Int) :FunHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return FunHolder(view)
    }

    override fun onBindViewHolder(holder :FunHolder, position :Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class FunDiffUtil: DiffUtil.ItemCallback<AnyItem>() {
        override fun areItemsTheSame(oldItem :AnyItem, newItem :AnyItem) :Boolean {
            return oldItem.content == newItem.content
        }

        override fun areContentsTheSame(oldItem :AnyItem, newItem :AnyItem) :Boolean {
           return oldItem == newItem
        }
    }
}