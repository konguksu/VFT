package com.example.vft

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class CommentActivity : AppCompatActivity(){

    private lateinit var exitBtn: ImageButton
    private lateinit var finBtn: Button
    private lateinit var edtTitle:TextView
    private lateinit var edtContent:TextView
    private lateinit var edtCount:TextView
    private lateinit var edtComment:EditText

    private lateinit var db: FirebaseFirestore
    private lateinit var docId: String
    private lateinit var userID: String

    private val writeValue: Long = 5 //코멘트 작성 성장값

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
        userID = Firebase.auth.currentUser!!.email.toString() //유저 이메일(아이디)

        exitBtn.setOnClickListener {
            //**닫기 버튼 클릭 시 작성을 취소하시겠어요? 대화상자 사용 여부 확인**
            val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_comment_quit, null)

            val dlg = AlertDialog.Builder(this).setView(dlgView)

            val alertDialog = dlg.create()
            alertDialog.show()

            val yesBtn: Button = dlgView.findViewById(R.id.yesBtn)
            val noBtn: Button = dlgView.findViewById(R.id.noBtn)

            yesBtn.setOnClickListener {
                alertDialog.dismiss()
                // 메인 화면으로 이동
                startActivity(Intent(this, ListActivity::class.java))
                finish()
            }

            noBtn.setOnClickListener {
                alertDialog.dismiss()
            }
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
                //대화상자
                val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_comment_finish, null)

                val dlg = AlertDialog.Builder(this).setView(dlgView)

                val alertDialog = dlg.create()
                alertDialog.show()

                val continueBtn: Button = dlgView.findViewById(R.id.continueBtn)
                val quitBtn: Button = dlgView.findViewById(R.id.quitBtn)

                //대화상자 - 더 작성하기 버튼
                continueBtn.setOnClickListener {
                    alertDialog.dismiss()
                    //작성한 코멘트 데이터베이스에 등록
                    db.collection("troubleList").document(docId).update("Comment",edtComment.text.toString())
                    db.collection("troubleList").document(docId).update("commentDate",FieldValue.serverTimestamp())
                    //성장도 데이터베이스 업데이트
                    resetLimitAndUpdateGrowth()
                    //코멘트 작성하지 않은 고민 리스트로 돌아가기
                    startActivity(Intent(this, ListActivity::class.java))
                    finish()
                }

                //대화상자 - 꿀 주기 버튼
                quitBtn.setOnClickListener {
                    //작성한 코멘트 데이터베이스에 등록
                    db.collection("troubleList").document(docId).update("Comment",edtComment.text.toString())
                    db.collection("troubleList").document(docId).update("commentDate",FieldValue.serverTimestamp())
                    //성장도 데이터베이스 업데이트
                    resetLimitAndUpdateGrowth()
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
                    Log.w("CommentActivity", "fetchDataFail", exception)
                }
    }

    //dailyLimit 초기화 되었는지 체크 & 초기화
    private fun resetLimitAndUpdateGrowth(){
        val docRef = db.collection("dailyLimit").document(userID)

        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val current = changeDateFormat(Date(System.currentTimeMillis()))
                        val lastUpdated = changeDateFormat(document.getTimestamp("lastUpdated")!!.toDate())

                        //현재 날짜가 마지막 초기화 날짜와 다르다면 dailyLimit 필드 초기화 & 마지막 초기화 날짜 설정
                        if(current != lastUpdated){
                            docRef.update("dailyLimit",DAILY_LIMIT)
                            docRef.update("lastUpdated",FieldValue.serverTimestamp())
                            updateGrowthDB(DAILY_LIMIT.toLong()) //growth db 갱신
                        }
                        else{
                            updateGrowthDB(document.getLong("dailyLimit")!!)//growth db 갱신
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("CommentActivity", "fetchLimitDataFail", exception)
                }
    }

    //타임스탬프 -> yyyyMMdd로 포맷 변환
    @SuppressLint("SimpleDateFormat")
    private fun changeDateFormat(data: Date):String{
        val dateFormat = "yyyyMMdd"
        val simpleDateFormat = SimpleDateFormat(dateFormat)

        return simpleDateFormat.format(data)
    }

    //성장도 데이터베이스 업데이트
    private fun updateGrowthDB(limitLeft:Long){
        //조건에 맞는 도큐먼트 찾기
        val query = db.collection("growthInfo")
                .whereEqualTo("ID",userID)
                .whereLessThan("growth",100)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val firstDoc = querySnapshot.documents[0] // 첫 번째 도큐먼트 가져오기
                val docId = firstDoc.id
                val docReference = db.collection("growthInfo").document(docId)

                //dailyLimit가 고민 작성 후 얻는 값보다 많이 남았을 때
                if(limitLeft > writeValue){
                    docReference.update("growth", FieldValue.increment(writeValue))
                    if(firstDoc.getLong("growth")!! > 100){docReference.update("growth",100)}
                    db.collection("dailyLimit").document(userID).update("dailyLimit",FieldValue.increment(-writeValue))
                }
                //dailyLimit가 고민 작성 후 얻는 값보다 같거나 적게 남았을 때
                else{
                    docReference.update("growth", FieldValue.increment(limitLeft))
                    if(firstDoc.getLong("growth")!! > 100){docReference.update("growth",100)}
                    db.collection("dailyLimit").document(userID).update("dailyLimit",0)
                    Toast.makeText(this,"하루 성장 최대치를 도달하였습니다",Toast.LENGTH_SHORT).show()
                }

                //성장도 체크
                isGrowFinish(docReference)

            } else {
                Log.w("CommentActivity", "No documents found")
            }
        }.addOnFailureListener { e ->
            Log.w("CommentActivity", "fetchGrowthDataFail", e)
        }

    }

    //성장도가 100을 달성했는지 체크, 달성하면 endDate 추가하고 새로운 growth 데이터베이스 생성
    private fun isGrowFinish(docRef:DocumentReference){
        docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if(document.getLong("growth")!!.compareTo(100) == 0 ){
                            docRef.update("endDate",FieldValue.serverTimestamp())
                            Toast.makeText(this,"성장 완료! 기록 모아보기 해금!",Toast.LENGTH_SHORT).show()

                            val growthInfo = hashMapOf(
                                    "ID" to userID,
                                    "growth" to 0,
                                    "startDate" to FieldValue.serverTimestamp(),
                                    "endDate" to null
                            )
                            db.collection("growthInfo").document().set(growthInfo)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("CommentActivity", "fetchGrowthDataFail", exception)
                }
    }
}