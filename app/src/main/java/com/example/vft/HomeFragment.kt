package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(){

    private lateinit var salmonProg: ProgressBar
    private lateinit var numSalmon: TextView
    private lateinit var honeyProg: ProgressBar
    private lateinit var numHoney: TextView

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
        salmonProg = view.findViewById(R.id.salmonProg)
        numSalmon = view.findViewById(R.id.numSalmon)
        honeyProg = view.findViewById(R.id.honeyProg)
        numHoney = view.findViewById(R.id.numHoney)
        val writeBtn: Button = view.findViewById(R.id.writeBtn)
        val listBtn: Button = view.findViewById(R.id.listBtn)

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

        writeBtn.setOnClickListener {
            val intent = Intent(activity, WriteActivity::class.java)
            startActivity(intent)
        }

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

                fetchNumFromFirestore(document.getTimestamp("startDate")!!)
                val growth = document.getLong("growth")

                //**성장도 체크해서 이미지 변경하는 코드 추가** 0/20/40/60/80
                if(growth!! >= 80){
                    image.setImageResource(R.drawable.ch0)
                }
                else if(growth >= 60){
                    image.setImageResource(R.drawable.ch1)
                }
                else if(growth >= 40){
                    image.setImageResource(R.drawable.ch0)
                }
                else if(growth >= 20){
                    image.setImageResource(R.drawable.ch1)
                }
                else {
                    image.setImageResource(R.drawable.ch1)
                }

            } else {
                Log.w("HomeFragment", "No documents found")
            }
        }.addOnFailureListener { e ->
            Log.w("HomeFragment", "fetchGrowthDataFail", e)
        }


    }


    //데이터베이스에서 해당 유저가 작성한 고민, 코멘트 개수 불러오기(일러스트 해금하면 초기화)
    private fun fetchNumFromFirestore(startDate:com.google.firebase.Timestamp) {
        var countWrite = 0
        var countComment = 0

        //연어 프로그래스바 관련 코드
        db.collection("troubleList")
                .whereEqualTo("userID",userID) //유저 아이디 일치, 고민
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        Log.w("HomeFragment", "fetchDataFail:연어", exception)
                    }

                    if (snapshot != null) {
                        for (document in snapshot.documents) {
                            if(document.getTimestamp("writeDate")!=null){
                                val writeDate = document.getTimestamp("writeDate")!! //고민 작성 날짜
                                //시작 날짜 이후에 작성된 고민인 경우
                                if(startDate.toDate() <= writeDate.toDate()){
                                    countWrite++
                                }
                            }

                        }
                        numSalmon.text = countWrite.toString()
                        countWrite = 0
                    }
                }


        //꿀 프로그래스바 관련 코드
        db.collection("troubleList")
                .whereEqualTo("userID",userID).whereNotEqualTo("Comment","") //유저 아이디 일치, 코멘트 있는 고민
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        Log.w("HomeFragment", "fetchDataFail:꿀", exception)
                    }

                    if (snapshot != null) {
                        for (document in snapshot.documents) {
                            if(document.getTimestamp("commentDate")!=null){
                                val commentDate = document.getTimestamp("commentDate")!! //코멘트 작성 날짜
                                if(startDate.toDate() <= commentDate.toDate()){
                                    countComment++
                                }
                            }

                        }
                        numHoney.text = countComment.toString()
                        countComment = 0
                    }
                }

    }

    private fun fetchStartDateAndNum(){
        //현재 활성화한 성장도 데이터 불러오기
        val query = db.collection("growthInfo")
                .whereEqualTo("ID",userID)
                .whereLessThan("growth",100)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents[0] // 첫 번째 도큐먼트 가져오기
                fetchNumFromFirestore(document.getTimestamp("startDate")!!) //성장 시작 날짜
            } else {
                Log.w("HomeFragment", "No documents found")
            }
        }.addOnFailureListener { e ->
            Log.w("HomeFragment", "fetchGrowthDataFail", e)
        }

    }

}