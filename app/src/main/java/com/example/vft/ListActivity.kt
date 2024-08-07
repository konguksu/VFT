package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase

class ListActivity : AppCompatActivity() {

    private lateinit var adapter: ListAdapter
    private val itemList = arrayListOf<ListItem>()
    private lateinit var db: FirebaseFirestore
    private lateinit var userID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val nickName = findViewById<TextView>(R.id.nickName)
        val exitBtn = findViewById<ImageButton>(R.id.exitBtn)
        val listView = findViewById<ListView>(R.id.listView)

        adapter = ListAdapter(this, itemList, 1)
        listView.adapter = adapter

        db = FirebaseFirestore.getInstance()
        userID = Firebase.auth.currentUser!!.email.toString() //유저 이메일(아이디)

        // 데이터 가져오기
        fetchDataFromFirestore()

        //데이터베이스에서 닉네임 가져와서 띄우기
        db.collection("userInfo").document(userID).get().addOnSuccessListener {document ->
            if (document != null) {
                nickName.text = document.getString("nickname").toString()
            }
        }.addOnFailureListener { exception ->
            Log.w("ListActivity", "fetchDataFail", exception)
        }

        //종료버튼
        exitBtn.setOnClickListener {
            // 메인 화면으로 이동
            startActivity(Intent(this, MainScreenActivity::class.java))
            finish()
        }

        // 고민 클릭 시 고민 전문화면으로 이동
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = itemList[position]
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("id",selectedItem.itemID)
            startActivity(intent)
            finish()
        }
    }

    //데이터베이스에서 해당 유저가 작성한 글 가져와서 리스트에 띄우기
    private fun fetchDataFromFirestore() {
        db.collection("troubleList")
                .whereEqualTo("userID",userID).whereEqualTo("Comment","") //유저 아이디 일치, 코멘트 없는 고민
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        Log.w("ListActivity", "fetchDataFail", exception)
                    }

                    if (snapshot != null) {
                        itemList.clear()
                        for (document in snapshot.documents) {
                            val title = document.getString("title") ?: ""
                            val content = document.getString("Content") ?: ""
                            val id = document.id
                            val listItem = ListItem(title, content,id)
                            itemList.add(listItem)

                        }
                        adapter.notifyDataSetChanged()
                    }
                }
    }
}
