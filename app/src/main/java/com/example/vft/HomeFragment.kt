package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class HomeFragment : Fragment(){

    private lateinit var daySalmon: TextView
    private lateinit var dayHoney: TextView
    private lateinit var progress: ProgressBar
    private lateinit var dayGrowth: TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nickName: TextView = view.findViewById(R.id.nickName)
        daySalmon = view.findViewById(R.id.daySalmon)
        dayHoney = view.findViewById(R.id.dayHoney)
        progress = view.findViewById(R.id.progress)
        dayGrowth = view.findViewById(R.id.dayGrowth)
        val writeBtn: FloatingActionButton = view.findViewById(R.id.writeBtn)
        val listBtn: FloatingActionButton = view.findViewById(R.id.listBtn)

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

        //고민작성하기 버튼
        writeBtn.setOnClickListener {
            val intent = Intent(activity, WriteActivity::class.java)
            startActivity(intent)
        }

        //코멘트작성하기 버튼
        listBtn.setOnClickListener {
            val intent = Intent(activity, ListActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val db = Firebase.firestore
        val userID = Firebase.auth.currentUser!!.email.toString()
        val image = view!!.findViewById<ImageView>(R.id.mainImg)

        //조건에 맞는 도큐먼트 찾기(성장도 100미만)
        val query = db.collection("growthInfo")
                .whereEqualTo("ID",userID)
                .whereLessThan("growth",100)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0] // 첫 번째 도큐먼트 가져오기

                //하루 연어, 꿀 성장도
                fetchNumFromFirestore(document.getTimestamp("startDate")!!)
                val growth = document.getLong("growth")

                //**성장도 체크해서 이미지 변경하는 코드 추가** 0/20/40/60/80
                when {
                    growth!! >= 80 -> {
                        image.setImageResource(R.drawable.ch0)
                    }
                    growth >= 60 -> {
                        image.setImageResource(R.drawable.ch1)
                    }
                    growth >= 40 -> {
                        image.setImageResource(R.drawable.ch0)
                    }
                    growth >= 20 -> {
                        image.setImageResource(R.drawable.ch1)
                    }
                    else -> {
                        image.setImageResource(R.drawable.ch1)
                    }
                }

                // 성장도와 dailyLimit을 가져와서 프로그레스 업데이트
                fetchGrowthAndLimit()

            } else {
                Log.w("HomeFragment", "No documents found")
            }
        }.addOnFailureListener { e ->
            Log.w("HomeFragment", "fetchGrowthDataFail", e)
        }


    }

    //데이터베이스에서 해당 유저가 작성한 고민, 코멘트 개수 불러오기(일러스트 해금하면 초기화)
    private fun fetchNumFromFirestore(startDate: Timestamp) {
        var countWrite = 0
        var countComment = 0

        //연어 프로그래스바 관련 코드
        db.collection("troubleList")
            .whereEqualTo("userID", userID) //유저 아이디 일치, 고민
            .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                if (exception != null) {
                    Log.w("HomeFragment", "fetchDataFail:연어", exception)
                }

                if (snapshot != null) {
                    for (document in snapshot.documents) {
                        if (document.getTimestamp("writeDate") != null) {
                            val writeDate = document.getTimestamp("writeDate")!! //고민 작성 날짜
                            //시작 날짜 이후에 작성된 고민인 경우
                            if (startDate.toDate() <= writeDate.toDate()) {
                                countWrite++
                            }
                        }
                    }
                    daySalmon.text = countWrite.toString()
                    countWrite = 0
                }
            }

        //꿀 프로그래스바 관련 코드
        db.collection("troubleList")
            .whereEqualTo("userID", userID).whereNotEqualTo("Comment", "") //유저 아이디 일치, 코멘트 있는 고민
            .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                if (exception != null) {
                    Log.w("HomeFragment", "fetchDataFail:꿀", exception)
                }

                if (snapshot != null) {
                    for (document in snapshot.documents) {
                        if (document.getTimestamp("commentDate") != null) {
                            val commentDate = document.getTimestamp("commentDate")!! //코멘트 작성 날짜
                            if (startDate.toDate() <= commentDate.toDate()) {
                                countComment++
                            }
                        }
                    }
                    dayHoney.text = countComment.toString()
                    countComment = 0
                }
            }
    }

    // 성장도와 dailyLimit을 가져와서 프로그레스 업데이트
    private fun fetchGrowthAndLimit() {
        val growthQuery = db.collection("growthInfo")
            .whereEqualTo("ID", userID)
            .whereLessThan("growth", 100)

        growthQuery.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0] // 첫 번째 도큐먼트 가져오기
                val growth = document.getLong("growth")?.toInt() ?: 0

                // dailyLimit 가져오기
                db.collection("dailyLimit").document(userID).get().addOnSuccessListener { limitDocument ->
                    val dailyLimit = limitDocument.getLong("dailyLimit")?.toInt() ?: 0
                    updateProgress(growth, dailyLimit)
                }.addOnFailureListener { e ->
                    Log.w("HomeFragment", "fetchDailyLimitFail", e)
                }
            }
        }.addOnFailureListener { e ->
            Log.w("HomeFragment", "fetchGrowthDataFail", e)
        }
    }

    //프로그래스 업데이트 함수
    private fun updateProgress(growth: Int, dailyLimit: Int) {
        progress.progress = growth // 현재 성장도
        progress.secondaryProgress = growth + dailyLimit // 오늘 하루 성장 가능 최댓값 표시
        dayGrowth.text = growth.toString()
    }


}