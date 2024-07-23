package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LogIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        val edtId = findViewById<EditText>(R.id.edtId)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnOk = findViewById<Button>(R.id.btnOk)

        btnOk.setOnClickListener {
            if (edtId != null && edtPass != null)
            {
                //아이디, 비밀번호 확인 후 가입 이력이 있는 경우 - 메인 화면으로 넘어감
                startActivity(Intent(this, MainScreen::class.java))
                finish()

                // 둘다 입력했는데 가입 이력이 없는 경우 - 회원가입해야됨
            }
            // 아이디, 비밀번호를 입력하지 않았을 때
            else
            {
                Toast.makeText(this,"아이디, 비밀번호를 정확히 입력하세요.",Toast.LENGTH_SHORT).show()
            }
        }

    }
}