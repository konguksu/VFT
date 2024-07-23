package com.example.vft

import android.app.ProgressDialog
import android.content.Intent
import android.net.wifi.hotspot2.pps.Credential
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Tag
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

class RegisterActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var isIDAvailable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtID = findViewById<EditText>(R.id.edtId)
        val edtPwd = findViewById<EditText>(R.id.edtPass)
        val edtPwdCheck = findViewById<EditText>(R.id.edtPassRe)
        val edtNickName = findViewById<EditText>(R.id.edtName)
        val btnIdCheck = findViewById<Button>(R.id.btnCheck)
        val btnRegister = findViewById<Button>(R.id.btnStart)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        //중복 확인 버튼
        btnIdCheck.setOnClickListener {
            val ID = edtID.text.toString().trim()

            //userInfo 데이터베이스 검색해서 같은 ID 존재하는지 검사
            db.collection("userInfo").whereEqualTo("ID", ID).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    //같은 ID 존재할 시
                    if (document!!.size() != 0) {
                        Toast.makeText(this, "존재하는 이메일", Toast.LENGTH_SHORT).show()
                    }
                    //같은 ID 없으면 isIDAvailable true로
                    else {
                        isIDAvailable = true
                        Toast.makeText(this, "사용 가능한 이메일", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Log.d("RegisterActivity", "Cached get failed: ", task.exception)
                }


            }
        }

        //가입 버튼(시작하기 버튼)
        btnRegister.setOnClickListener {
            val ID = edtID.text.toString().trim()
            val pwd = edtPwd.text.toString().trim()
            val pwdCheck = edtPwdCheck.text.toString().trim()
            val nickname = edtNickName.text.toString().trim()

            //중복 확인 검사 통과 못했다면
            if (!isIDAvailable) {
                Toast.makeText(this, "아이디 중복 확인 필요", Toast.LENGTH_SHORT).show()
            }
            //비밀번호와 비밀번호 재확인 값이 일치하는지 확인
            else if (!pwd.equals(pwdCheck))
            {
                Toast.makeText(this, "비밀번호 불일치", Toast.LENGTH_SHORT).show()
            }
            //중복 확인 검사도 통과, 비밀번호 재확인도 완료
            else
            {
                auth.createUserWithEmailAndPassword(ID, pwd).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //유저 정보 해시맵으로
                        val userInfo = hashMapOf(
                                "ID" to ID,
                                "password" to pwd,
                                "nickname" to nickname,
                                "joinDate" to FieldValue.serverTimestamp(),
                        )
                        //userInfo 데이터베이스에 등록
                        db.collection("userInfo").document(ID).set(userInfo)

                        Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
                        //로그인 화면으로 이동
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()

                    }
                    else
                    {
                        Toast.makeText(this, "아이디와 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                    }
                }


            }

        }


    }
}
