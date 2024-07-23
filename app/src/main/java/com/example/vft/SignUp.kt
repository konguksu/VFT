package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUp : AppCompatActivity() {
    private var isChecked = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        val edtId = findViewById<EditText>(R.id.edtId)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val edtPassRe = findViewById<EditText>(R.id.edtPassRe)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnCheck = findViewById<Button>(R.id.btnCheck)

        btnCheck.setOnClickListener {
            //중복 아이디가 없으면 -> 토스트 메시지: 사용가능한 아이디입니다
            isChecked = 1
            Toast.makeText(this,"사용가능한 아이디입니다", Toast.LENGTH_SHORT).show()
            //중복 아이디가 있으면 -> 토스트 메시지: 사용불가한 아이디입니다
        }

        btnStart.setOnClickListener {
            //아이디 중복확인, 비밀번호=비밀번호재입력 모두 완료 -> 로그인화면으로 넘어가기
            if (isChecked == 1 && edtPass.text.toString() == edtPassRe.text.toString()) {
                startActivity(Intent(this, LogIn::class.java))
                finish()
            }
            //아이디 중복확인 안함 -> 토스트메시지
            else if (isChecked == 0) {
                Toast.makeText(this,"아이디 중복확인을 하지 않았습니다", Toast.LENGTH_SHORT).show()
            }
            //비밀번호!=비밀번호재입력 -> 토스트메시지
            else if (edtPass.text.toString() != edtPassRe.text.toString()) {
                Toast.makeText(this,"비밀번호를 다시 확인하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}