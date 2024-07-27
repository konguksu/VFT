package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ListActivity : AppCompatActivity() {

    private lateinit var adapter: ListAdapter
    private val itemList = arrayListOf<ListItem>()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val exitBtn = findViewById<Button>(R.id.exitBtn)
        val listView = findViewById<ListView>(R.id.listView)
        val viewAllBtn = findViewById<Button>(R.id.viewAllBtn)

        adapter = ListAdapter(this, itemList)
        listView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        // 데이터 가져오기
        fetchDataFromFirestore()

        exitBtn.setOnClickListener {
            // 메인 화면으로 이동
            startActivity(Intent(this, MainScreenActivity::class.java))
            finish()
        }

        // 고민 클릭 시 고민 전문화면으로 이동
    }

    //데이터베이스에서 그동안 작성한 글 가져와서 리스트에 띄우기
    private fun fetchDataFromFirestore() {
        db.collection("troubleList")
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        // Handle the error
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        itemList.clear()
                        for (document in snapshot.documents) {
                            val title = document.getString("title") ?: ""
                            val content = document.getString("Content") ?: ""
                            val listItem = ListItem(title, content)
                            itemList.add(listItem)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
    }

}
