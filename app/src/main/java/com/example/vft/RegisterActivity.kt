package com.example.vft

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.internal.TextWatcherAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

val DAILY_LIMIT = 10

class RegisterActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var isIDAvailable = false //아이디 중복 검사

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

        //id 변경하면 중복확인 초기화
        edtID.addTextChangedListener(@SuppressLint("RestrictedApi")
        object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, p1: Int, p2: Int, p3: Int) {
                isIDAvailable = false
            }
        })

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

            //중복 확인 통과했는지 검사
            if (!isIDAvailable) {
                Toast.makeText(this, "아이디 중복 확인 필요", Toast.LENGTH_SHORT).show()
            }
            //비밀번호 패턴 일치 검사
            else if (!isPwdOk(pwd)){
                Toast.makeText(this, "비밀번호는 6~20자 이내의 \n영문/숫자/특수문자 중 2가지 이상의 조합이어야 합니다", Toast.LENGTH_SHORT).show()
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
                        //하루 성장도 제한 해시맵으로
                        val dailyLimit = hashMapOf(
                                "ID" to ID,
                                "dailyLimit" to DAILY_LIMIT,
                                "lastUpdated" to FieldValue.serverTimestamp()
                        )
                        //성장도 해시맵으로
                        val growthInfo = hashMapOf(
                                "ID" to ID,
                                "growth" to 0,
                                "startDate" to FieldValue.serverTimestamp(),
                                "endDate" to null
                        )
                        //userInfo 데이터베이스에 등록
                        db.collection("userInfo").document(ID).set(userInfo)
                        //하루 성장도 제한 데이터베이스에 등록
                        db.collection("dailyLimit").document(ID).set(dailyLimit)
                        //성장 정보 데이터베이스 등록
                        db.collection("growthInfo").document().set(growthInfo)

                        Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()
                        auth.signOut() //자동으로 로그인 되므로 우선 로그아웃
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

    //비밀번호 규약 따르는지 검사하는 함수
    //비밀번호 규정 숫자/문자/특수문자 2가지 이상 조합한 6~20자
    private fun isPwdOk(pwd:String):Boolean{
        val pwdPattern1 =
                "^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z[0-9]]{6,20}$" // 영문 + 숫자
        val pwdPattern2 =
                "^(?=.*[0-9])(?=.*[$@$!%*#?&.])[[0-9]$@$!%*#?&.]{6,20}$" // 숫자 +특수문자
        val pwdPattern3 =
                "^(?=.*[A-Za-z])(?=.*[$@$!%*#?&.])[A-Za-z$@$!%*#?&.]{6,20}$" // 영문 + 특수문자
        val pwdPattern4 =
                "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{6,20}$" // 영문 + 숫자 + 특수문자

        return (Pattern.matches(pwdPattern1, pwd) ||
                Pattern.matches(pwdPattern2, pwd) ||
                Pattern.matches(pwdPattern3, pwd) ||
                Pattern.matches(pwdPattern4, pwd))
    }

}
