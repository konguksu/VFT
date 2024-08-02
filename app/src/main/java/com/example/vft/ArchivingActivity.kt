package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ArchivingActivity : AppCompatActivity() {

    private lateinit var nickName:TextView
    private lateinit var writeText:TextView
    private lateinit var writeMore:Button
    private lateinit var writeList:ListView
    private lateinit var commentText:TextView
    private lateinit var commentMore:Button
    private lateinit var commentList:ListView

    private lateinit var writeAdapter: ListAdapter
    private lateinit var commentAdapter: ListAdapter
    private val writeItemList = arrayListOf<ListItem>()
    private val commentItemList = arrayListOf<ListItem>()

    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archiving)

        nickName = findViewById(R.id.nickName)
        writeText = findViewById(R.id.writeText)
        writeMore = findViewById(R.id.writeMore)
        writeList = findViewById(R.id.writeList)
        commentText = findViewById(R.id.commentText)
        commentMore = findViewById(R.id.commentMore)
        commentList = findViewById(R.id.commentList)

        val selectedItem = intent.getParcelableExtra<IllustItem>("selectedItem")
        val startDate = selectedItem!!.startDate
        val endDate = selectedItem.endDate

        writeAdapter = ListAdapter(this, writeItemList,1)
        commentAdapter = ListAdapter(this, commentItemList,0)
        writeList.adapter = writeAdapter
        commentList.adapter = commentAdapter

        db = Firebase.firestore
        userID = Firebase.auth.currentUser!!.email.toString() //유저 이메일(아이디)
        //데이터베이스에서 닉네임 가져와서 띄우기
        db.collection("userInfo").document(userID).get().addOnSuccessListener {document ->
            if (document != null) {
                nickName.text = document.getString("nickname").toString()
            }
        }.addOnFailureListener { exception ->
            Log.w("ArchivingActivity", "fetchDataFail", exception)
        }

        //글 목록 보기
        fetchDataFromFirestore(startDate,endDate)

        //고민 더보기 클릭 시 작성된 고민 화면으로 이동
        writeMore.setOnClickListener {
            val intent = Intent(this, ListIndivActivity::class.java)
            intent.putExtra("EXTRA_TYPE", "writeMore")
            intent.putExtra("selectedItem",selectedItem)
            startActivity(intent)
        }

        //코멘트 더보기 클릭 시 작성된 코멘트 화면으로 이동
        commentMore.setOnClickListener {
            val intent = Intent(this, ListIndivActivity::class.java)
            intent.putExtra("EXTRA_TYPE", "commentMore")
            intent.putExtra("selectedItem",selectedItem)
            startActivity(intent)
        }
    }

    //데이터베이스에서 해당 유저가 작성한 고민, 코멘트 최대 3개 불러오기(특정 기간)
    private fun fetchDataFromFirestore(startDate:Timestamp,endDate:Timestamp) {
        //고민 불러오기
        db.collection("troubleList")
                .whereEqualTo("userID",userID) //유저 아이디 일치, 고민
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        Log.w("ArchivingActivity", "fetchDataFail:고민", exception)
                    }

                    if (snapshot != null) {
                        writeItemList.clear()
                        for (document in snapshot.documents) {
                            if(document.getTimestamp("writeDate")!=null){
                                val writeDate = document.getTimestamp("writeDate")!! //고민 작성 날짜
                                //특정 기간에 작성된 고민인 경우
                                if(startDate.toDate() <= writeDate.toDate() && endDate.toDate() >= writeDate.toDate() && writeItemList.size < 3){
                                    val title = document.getString("title")?: ""
                                    val content = document.getString("Content")?: ""
                                    val id = document.id
                                    val listItem = ListItem(title, content,id)
                                    writeItemList.add(listItem)
                                }
                            }
                        }
                        //기간동안 작성한 고민이 하나도 없을 경우
                        if(writeItemList.size == 0){
                            writeText.visibility = View.GONE
                            writeMore.visibility = View.GONE
                        }
                        writeAdapter.notifyDataSetChanged()
                    }
                }


        //코멘트 불러오기
        db.collection("troubleList")
                .whereEqualTo("userID",userID).whereNotEqualTo("Comment","") //유저 아이디 일치, 코멘트 있는 고민
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        Log.w("ArchivingActivity", "fetchDataFail:코멘트", exception)
                    }

                    if (snapshot != null) {
                        commentItemList.clear()
                        for (document in snapshot.documents) {
                            if(document.getTimestamp("commentDate")!=null){
                                val commentDate = document.getTimestamp("commentDate")!! //코멘트 작성 날짜
                                //특정 기간에 작성된 코멘트인 경우
                                if(startDate.toDate() <= commentDate.toDate() && endDate.toDate() >= commentDate.toDate() && commentItemList.size < 3){
                                    val comment = document.getString("Comment")?: ""
                                    val id = document.id
                                    val listItem = ListItem("", comment,id)
                                    commentItemList.add(listItem)
                                }
                            }
                        }
                        //기간동안 작성한 코멘트가 하나도 없을 경우
                        if(commentItemList.size == 0){
                            commentText.visibility = View.GONE
                            commentMore.visibility = View.GONE
                        }
                        commentAdapter.notifyDataSetChanged()
                    }
                }

    }
}
