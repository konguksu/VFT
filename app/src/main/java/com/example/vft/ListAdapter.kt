package com.example.vft

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ListAdapter (private val context: Context, private val itemList: ArrayList<ListItem>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view:View = LayoutInflater.from(context).inflate(R.layout.list_item, null)

        val itemTitle = view.findViewById<TextView>(R.id.itemTitle)
        val itemContent = view.findViewById<TextView>(R.id.itemContent)

        val item = itemList[position]
        itemTitle.text = item.itemTitle
        itemContent.text = item.itemContent

        return view
    }

    fun addItem(item: ListItem) {
        itemList.add(item)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }



}