package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WriteActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val nickName = findViewById<TextView>(R.id.nickName)
        val exitBtn = findViewById<Button>(R.id.exitBtn)
        val edtTitle = findViewById<EditText>(R.id.edtTitle)
        val edtContent = findViewById<EditText>(R.id.edtContent)
        val edtCount = findViewById<TextView>(R.id.count)
        val finBtn = findViewById<Button>(R.id.finBtn)

        db = Firebase.firestore
        val userID = Firebase.auth.currentUser!!.email //유저 이메일(아이디)

        //**데이터베이스에서 닉네임 가져와서 띄우기**

        exitBtn.setOnClickListener {
            val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_write_quit, null)

            val dlg = AlertDialog.Builder(this).setView(dlgView)

            val alertDialog = dlg.create()
            alertDialog.show()

            val yesBtn: Button = dlgView.findViewById(R.id.yesBtn)
            val noBtn: Button = dlgView.findViewById(R.id.noBtn)

            yesBtn.setOnClickListener {
                // 메인 화면으로 이동
                startActivity(Intent(this, MainScreenActivity::class.java))
                finish()
            }

            noBtn.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        //내용 입력
        edtContent.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                edtCount.text = edtContent.text.length.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        //완료 버튼
        finBtn.setOnClickListener {
            //제목 입력 안된 경우
            if (edtTitle.text.toString() == "") {
                Toast.makeText(this, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            //고민 내용 입력 안된 경우
            else if (edtContent.text.toString() == "") {
                Toast.makeText(this, "고민을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            //제목, 고민 둘 다 입력된 경우
            else {
                //고민 데이터 해시맵으로
                val troubleData = hashMapOf(
                        "userID" to userID,
                        "title" to edtTitle.text.toString(),
                        "Content" to edtContent.text.toString(),
                        "writeDate" to FieldValue.serverTimestamp(),
                        "Comment" to ""
                )
                //troubleList 데이터베이스에 등록
                db.collection("troubleList").document().set(troubleData)

                //대화상자
                val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_write_finish, null)

                val dlg = AlertDialog.Builder(this).setView(dlgView)

                val alertDialog = dlg.create()
                alertDialog.show()

                val continueBtn: Button = dlgView.findViewById(R.id.continueBtn)
                val quitBtn: Button = dlgView.findViewById(R.id.quitBtn)

                //대화상자 - 더 작성하기 버튼
                continueBtn.setOnClickListener {
                    //제목과 내용 초기화
                    edtTitle.text = null
                    edtContent.text = null

                    //대화상자 닫기
                    alertDialog.dismiss()
                }

                //대화상자 - 연어 주기 버튼
                quitBtn.setOnClickListener {

                    // 메인 화면으로 이동
                    startActivity(Intent(this, MainScreenActivity::class.java))
                    finish()
                }
            }
        }
    }
}
