package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue

class CommentActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val exitBtn = findViewById<Button>(R.id.exitBtn)
        val edtTitle = findViewById<EditText>(R.id.edtTitle)
        val edtContent = findViewById<EditText>(R.id.edtContent)
        val edtCount = findViewById<TextView>(R.id.count)
        val edtComment = findViewById<EditText>(R.id.edtComment)
        val finBtn = findViewById<Button>(R.id.finBtn)

        exitBtn.setOnClickListener {
            //대화상자 미사용시
            startActivity(Intent(this, ListActivity::class.java))
            finish()

            //**닫기 버튼 클릭 시 작성을 취소하시겠어요? 대화상자 사용 여부 확인**
        }

        //**데이터베이스에서 고민 제목, 내용 가져와서 띄우는 코드**

        //**고민 내용 글자수 띄우는 코드**

        //완료 버튼
        finBtn.setOnClickListener {
            val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_comment_finish, null)

            val dlg = AlertDialog.Builder(this).setView(dlgView)

            val alertDialog = dlg.create()
            alertDialog.show()

            val continueBtn: Button = dlgView.findViewById(R.id.continueBtn)
            val quitBtn: Button = dlgView.findViewById(R.id.quitBtn)

            //대화상자 - 더 작성하기 버튼
            continueBtn.setOnClickListener {
                //제목과 내용 초기화
//                    edtTitle.text = null
//                    edtContent.text = null

                //대화상자를 끄고 작성하던 글을 이어서 작성
                alertDialog.dismiss()
            }

            //대화상자 - 꿀 주기 버튼
            quitBtn.setOnClickListener {
                //코멘트 입력 안된 경우
                if (edtComment.text.toString() == "") {
                    Toast.makeText(this, "코멘트를 입력해주세요", Toast.LENGTH_SHORT).show()
                }
                //코멘트 입력된 경우
                else {
                    //**작성한 코멘트 데이터베이스에 등록하는 코드**

                    // 메인 화면으로 이동
                    startActivity(Intent(this, MainScreenActivity::class.java))
                    finish()
                }
            }
        }
    }
}