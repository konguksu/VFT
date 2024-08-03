package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListIndivActivity : AppCompatActivity() {

    private lateinit var adapter: ListAdapter
    private val itemList = arrayListOf<ListItem>()

    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_indiv)

        val title = findViewById<TextView>(R.id.title)
        val listView = findViewById<ListView>(R.id.listView)

        val clickMore = intent.getStringExtra("EXTRA_TYPE")
        val selectedItem = intent.getParcelableExtra<IllustItem>("selectedItem")
        val startDate = selectedItem!!.startDate
        val endDate = selectedItem.endDate

        db = Firebase.firestore
        userID = Firebase.auth.currentUser!!.email.toString() //유저 이메일(아이디)

        //어떤 더보기를 클릭했는지에 따라 보여지는 목록이 다름
        when (clickMore) {
            "writeMore" -> {
                // 화면 상단 타이틀 변경
                title.text = "작성된 고민"

                // 고민 더보기 클릭 시 고민 목록 띄우기
                adapter = ListAdapter(this, itemList, 1)
                listView.adapter = adapter

                fetchWrite(startDate,endDate)

            }
            "commentMore" -> {
                // 화면 상단 타이틀 변경
                title.text = "작성된 코멘트"

                // 코멘트 더보기 클릭 시 코멘트 목록 띄우기
                adapter = ListAdapter(this, itemList, 0)
                listView.adapter = adapter

                fetchComment(startDate,endDate)
            }
        }

        // 고민 클릭 시 고민 전문화면으로 이동
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = itemList[position]
            val intent = Intent(this, ReadOnlyActivity::class.java)
            intent.putExtra("id", selectedItem.itemID)
            startActivity(intent)
        }
    }

    //데이터베이스에서 해당 유저가 작성한 고민 불러오기(특정 기간)
    private fun fetchWrite(startDate: Timestamp, endDate: Timestamp) {
        //고민 불러오기
        db.collection("troubleList")
            .whereEqualTo("userID",userID) //유저 아이디 일치, 고민
            .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                if (exception != null) {
                    Log.w("ArchivingActivity", "fetchDataFail:고민", exception)
                }

                if (snapshot != null) {
                    itemList.clear()
                    for (document in snapshot.documents) {
                        val writeDate = document.getTimestamp("writeDate")!! //고민 작성 날짜
                        //특정 기간에 작성된 고민인 경우
                        if(startDate.toDate() <= writeDate.toDate() && endDate.toDate() >= writeDate.toDate()){
                            val title = document.getString("title")?: ""
                            val content = document.getString("Content")?: ""
                            val id = document.id
                            val listItem = ListItem(title, content,id)
                            itemList.add(listItem)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }




    }
    //데이터베이스에서 해당 유저가 작성한 코멘트 불러오기(특정 기간)
    private fun fetchComment(startDate: Timestamp, endDate: Timestamp){
        //코멘트 불러오기
        db.collection("troubleList")
            .whereEqualTo("userID",userID).whereNotEqualTo("Comment","") //유저 아이디 일치, 코멘트 있는 고민
            .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                if (exception != null) {
                    Log.w("ArchivingActivity", "fetchDataFail:코멘트", exception)
                }

                if (snapshot != null) {
                    itemList.clear()
                    for (document in snapshot.documents) {
                        if(document.getTimestamp("commentDate")!=null){
                            val commentDate = document.getTimestamp("commentDate")!! //코멘트 작성 날짜
                            //특정 기간에 작성된 코멘트인 경우
                            if(startDate.toDate() <= commentDate.toDate() && endDate.toDate() >= commentDate.toDate()){
                                val comment = document.getString("Comment")?: ""
                                val id = document.id
                                val listItem = ListItem("", comment,id)
                                itemList.add(listItem)
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}