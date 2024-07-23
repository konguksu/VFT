package com.example.vft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtId = findViewById<EditText>(R.id.edtId)
        val edtPwd = findViewById<EditText>(R.id.edtPass)
        val btnLogin = findViewById<Button>(R.id.btnOk)

        auth = FirebaseAuth.getInstance()

        //로그인 버튼 클릭
        btnLogin.setOnClickListener {
            val id = edtId.text.toString().trim()
            val pwd = edtPwd.text.toString().trim()

            //아이디 또는 비밀번호 중 하나라도 입력을 안한경우
            if(edtId.text.toString().equals("") || edtPwd.text.toString().equals("")){
                Toast.makeText(baseContext, "아이디와 비밀번호를 모두 입력해주세요", Toast.LENGTH_SHORT,).show()
            }
            //둘 다 입력한 경우
            else{
                //로그인 시도
                auth.signInWithEmailAndPassword(id,pwd).addOnCompleteListener(this) { task ->
                    //로그인 성공시
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "로그인 성공", Toast.LENGTH_SHORT,).show()
                        startActivity(Intent(this, MainScreen::class.java))
                        finish()
                    //로그인 실패시
                    } else {
                        Log.w("MainActivity", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "아이디, 비밀번호를 정확히 입력하세요.", Toast.LENGTH_SHORT,).show()
                    }
                }
            }

        }
    }
}