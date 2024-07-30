package com.example.vft

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class IllustAdapter (private val context: Context, private val illustList: ArrayList<IllustItem>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.illust_item, parent, false)

        val illust = view.findViewById<ImageView>(R.id.illust)
        val item = illustList[position]
        illust.setImageResource(item.illustId)

        return view
    }

    override fun getCount(): Int {
        return illustList.size
    }

    override fun getItem(position: Int): Any {
        return illustList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}