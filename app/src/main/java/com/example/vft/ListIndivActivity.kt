package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ListIndivActivity : AppCompatActivity() {

    private lateinit var adapter: ListAdapter
    private val itemList = arrayListOf<ListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_indiv)

        val title = findViewById<TextView>(R.id.title)
        val listView = findViewById<ListView>(R.id.listView)

        val clickMore = intent.getStringExtra("EXTRA_TYPE")

        //어떤 더보기를 클릭했는지에 따라 보여지는 목록이 다름
        when (clickMore) {
            "writeMore" -> {
                // 화면 상단 타이틀 변경
                title.text = "작성된 고민"

                // 고민 더보기 클릭 시 고민 목록 띄우기
                adapter = ListAdapter(this, itemList, 1)
                listView.adapter = adapter

                //**ListActivity와 동일**

            }
            "commentMore" -> {
                // 화면 상단 타이틀 변경
                title.text = "작성된 코멘트"

                // 코멘트 더보기 클릭 시 코멘트 목록 띄우기
                adapter = ListAdapter(this, itemList, 1)
                listView.adapter = adapter

                //**title은 null값으로, content는 db에서 가져온 값으로 넣어주면 됨**
                //ex. val listItem = ListItem(null, content, id)
            }
        }

        // 고민 클릭 시 고민 전문화면으로 이동
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = itemList[position]
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("id", selectedItem.itemID)
            startActivity(intent)
            finish()
        }
    }
}