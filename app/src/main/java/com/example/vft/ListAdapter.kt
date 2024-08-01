package com.example.vft

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ListAdapter(
        private val context: Context,
        private val itemList: ArrayList<ListItem>,
        private val layoutType: Int
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // 레이아웃 선택
        val layoutResId = if (layoutType == 1) {
            R.layout.write_item
        } else {
            R.layout.comment_item
        }

        // convertView가 null이면 새로운 뷰 인플레이트
        val view: View = convertView ?: LayoutInflater.from(context).inflate(layoutResId, parent, false)

        // ViewHolder 패턴을 사용하여 성능 최적화
        val viewHolder: ViewHolder = if (convertView == null) {
            val holder = ViewHolder()
            holder.itemTitle = view.findViewById(R.id.itemTitle)
            holder.itemContent = view.findViewById(R.id.itemContent)
            view.tag = holder
            holder
        } else {
            view.tag as ViewHolder
        }

        val item = itemList[position]
        viewHolder.itemTitle?.text = item.itemTitle
        viewHolder.itemContent?.text = item.itemContent

        return view
    }

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // ViewHolder 클래스 정의
    private class ViewHolder {
        var itemTitle: TextView? = null
        var itemContent: TextView? = null
    }
}
