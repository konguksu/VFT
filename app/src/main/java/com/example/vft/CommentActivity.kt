package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CommentActivity : AppCompatActivity(){

    private lateinit var exitBtn: Button
    private lateinit var finBtn: Button
    private lateinit var edtTitle:EditText
    private lateinit var edtContent:EditText
    private lateinit var edtCount:TextView
    private lateinit var edtComment:EditText

    private lateinit var db: FirebaseFirestore
    private lateinit var docId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        exitBtn = findViewById(R.id.exitBtn)
        edtTitle = findViewById(R.id.edtTitle)
        edtContent = findViewById(R.id.edtContent)
        edtCount = findViewById(R.id.count)
        edtComment = findViewById(R.id.edtComment)
        finBtn = findViewById(R.id.finBtn)

        db = FirebaseFirestore.getInstance()
        docId = intent.getStringExtra("id").toString()

        exitBtn.setOnClickListener {
            //대화상자 미사용시
            startActivity(Intent(this, ListActivity::class.java))
            finish()

            //**닫기 버튼 클릭 시 작성을 취소하시겠어요? 대화상자 사용 여부 확인**
        }

        //**데이터베이스에서 고민 제목, 내용 가져와서 띄우는 코드**
        fetchDataFromFirestore()


        //완료 버튼
        finBtn.setOnClickListener {
            //코멘트 입력 안된 경우
            if (edtComment.text.toString() == "") {
                Toast.makeText(this, "코멘트를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            //코멘트 입력된 경우
            else {
                //**작성한 코멘트 데이터베이스에 등록하는 코드**
                db.collection("troubleList").document(docId).update("Comment",edtComment.text.toString())

                val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_comment_finish, null)

                val dlg = AlertDialog.Builder(this).setView(dlgView)

                val alertDialog = dlg.create()
                alertDialog.show()

                val continueBtn: Button = dlgView.findViewById(R.id.continueBtn)
                val quitBtn: Button = dlgView.findViewById(R.id.quitBtn)

                //대화상자 - 더 작성하기 버튼
                continueBtn.setOnClickListener {
                    //대화상자를 끄고 작성하던 글을 이어서 작성
                    startActivity(Intent(this, ListActivity::class.java))
                    finish()
                }

                //대화상자 - 꿀 주기 버튼
                quitBtn.setOnClickListener {
                    // 메인 화면으로 이동
                    startActivity(Intent(this, MainScreenActivity::class.java))
                    finish()
                }
            }

        }
    }

    private fun fetchDataFromFirestore(){
        val docRef = db.collection("troubleList").document(docId)
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        edtTitle.setText(document.getString("title"))
                        edtContent.setText(document.getString("Content"))
                        edtCount.text = edtContent.text.length.toString() //글자수 표시
                    }
                }
                .addOnFailureListener { exception ->

                }
    }
}