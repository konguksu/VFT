package com.example.vft
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Debug
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class WriteActivity : AppCompatActivity() {

    private lateinit var exitBtn:Button
    private lateinit var edtTitle:EditText
    private lateinit var edtContent:EditText
    private lateinit var edtCount:TextView
    private lateinit var finBtn:Button

    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    private val writeValue: Long = 10 //고민 작성 성장값

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val nickName = findViewById<TextView>(R.id.nickName)
        exitBtn = findViewById<Button>(R.id.exitBtn)
        edtTitle = findViewById(R.id.edtTitle)
        edtContent = findViewById(R.id.edtContent)
        edtCount = findViewById(R.id.count)
        finBtn = findViewById(R.id.finBtn)

        db = Firebase.firestore
        userID = Firebase.auth.currentUser!!.email.toString() //유저 이메일(아이디)

        //데이터베이스에서 닉네임 가져와서 띄우기
        db.collection("userInfo").document(userID).get().addOnSuccessListener {document ->
            if (document != null) {
                nickName.text = document.getString("nickname").toString()
            }
        }.addOnFailureListener { exception ->
                    Log.w("WriteActivity", "fetchDataFail", exception)
                }

        //x 버튼
        exitBtn.setOnClickListener {
            val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_write_quit, null)

            val dlg = AlertDialog.Builder(this).setView(dlgView)

            val alertDialog = dlg.create()
            alertDialog.show()

            val yesBtn: Button = dlgView.findViewById(R.id.yesBtn)
            val noBtn: Button = dlgView.findViewById(R.id.noBtn)

            yesBtn.setOnClickListener {
                alertDialog.dismiss()
                // 메인 화면으로 이동
                startActivity(Intent(this, MainScreenActivity::class.java))
                finish()
            }

            noBtn.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        //내용 입력 -> 글자 수 표시
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
                //대화상자 띄우기
                val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_write_finish, null)

                val dlg = AlertDialog.Builder(this).setView(dlgView)

                val alertDialog = dlg.create()
                alertDialog.show()

                val continueBtn: Button = dlgView.findViewById(R.id.continueBtn)
                val quitBtn: Button = dlgView.findViewById(R.id.quitBtn)

                //대화상자 - 더 작성하기 버튼
                continueBtn.setOnClickListener {
                    updateTroubleDB()
                    resetLimitAndUpdateGrowth()
                    //제목과 내용 초기화
                    edtTitle.text = null
                    edtContent.text = null

                    //대화상자를 끄기
                    alertDialog.dismiss()
                }

                //대화상자 - 연어 주기 버튼
                quitBtn.setOnClickListener {
                    updateTroubleDB()
                    resetLimitAndUpdateGrowth()
                    // 메인 화면으로 이동
                    finish()

                    //대화상자를 끄기
                    alertDialog.dismiss()
                }

            }

        }
    }
    //작성한 고민 데이터베이스에 등록
    private fun updateTroubleDB(){
        //고민 데이터 해시맵으로
        val troubleData = hashMapOf(
                "userID" to userID,
                "title" to edtTitle.text.toString(),
                "Content" to edtContent.text.toString(),
                "writeDate" to FieldValue.serverTimestamp(),
                "Comment" to "",
                "commentDate" to null
        )
        //troubleList 데이터베이스에 등록
        db.collection("troubleList").document().set(troubleData)
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
                    Log.w("WriteActivity", "fetchLimitDataFail", exception)
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
                Log.w("WriteActivity", "No documents found")
            }
        }.addOnFailureListener { e ->
            Log.w("WriteActivity", "fetchGrowthDataFail", e)
        }

    }

    //성장도가 100을 달성했는지 체크, 달성하면 endDate 추가하고 새로운 growth 데이터베이스 생성
    private fun isGrowFinish(docRef: DocumentReference){
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
                    Log.w("WriteActivity", "fetchGrowthDataFail", exception)
                }
    }


}
