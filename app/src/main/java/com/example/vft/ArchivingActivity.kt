package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ArchivingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archiving)

        val nickName = findViewById<TextView>(R.id.nickName)
        val writeText = findViewById<TextView>(R.id.writeText)
        val writeMore = findViewById<TextView>(R.id.writeMore)
        val writeList = findViewById<ListView>(R.id.writeList)
        val commentText = findViewById<TextView>(R.id.commentText)
        val commentMore = findViewById<TextView>(R.id.commentMore)
        val commentList = findViewById<ListView>(R.id.commentList)

        //**데이터베이스에서 닉네임 가져와서 띄우기**

        //글 목록 보기
        //**작성된 고민, 코멘트가 없는 경우** -> DB 코멘트, 고민글 존재 여부 확인이 우선
//        if(작성된 코멘트 없음) {
//            //문구 안보이게 설정
//            commentText.visibility = View.INVISIBLE
//            commentMore.visibility = View.INVISIBLE
//            if (고민 없음) {
//                //문구 안보이게 설정
//                writeText.visibility = View.INVISIBLE
//                writeMore.visibility = View.INVISIBLE
//            } else { //고민은 있지만 코멘트는 없음
//                //문구 보이게 설정
//                writeText.visibility = View.VISIBLE
//                writeMore.visibility = View.VISIBLE
//
//                //**고민 리스트 띄우기**
//
//            }
//        } else { //고민, 코멘트 둘 다 있음
//            //**고민, 코멘트 리스트 띄우기**
//
//        }

        //**각 항목 클릭 시 고민 전문으로 이동??**

        //고민 더보기 클릭 시 작성된 고민 화면으로 이동
        writeMore.setOnClickListener {
            val intent = Intent(this, ListIndivActivity::class.java)
            intent.putExtra("EXTRA_TYPE", "writeMore")
            startActivity(intent)
        }

        //코멘트 더보기 클릭 시 작성된 코멘트 화면으로 이동
        commentMore.setOnClickListener {
            val intent = Intent(this, ListIndivActivity::class.java)
            intent.putExtra("EXTRA_TYPE", "commentMore")
            startActivity(intent)
        }
    }
}
