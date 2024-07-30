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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(){


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
        val salmonProg: ProgressBar = view.findViewById(R.id.salmonProg)
        val numSalmon: TextView = view.findViewById(R.id.numSalmon)
        val honeyProg: ProgressBar = view.findViewById(R.id.honeyProg)
        val numHoney: TextView = view.findViewById(R.id.numHoney)
        val writeBtn: Button = view.findViewById(R.id.writeBtn)
        val listBtn: Button = view.findViewById(R.id.listBtn)

        //**데이터베이스에서 닉네임 가져와서 띄우기**

        //**연어 프로그래스바 관련 코드**

        //**꿀 프로그래스바 관련 코드**

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

        //데이터베이스 반영을 위해 시간 지연
        Handler(Looper.getMainLooper()).postDelayed({
            //조건에 맞는 도큐먼트 찾기
            val query = db.collection("growthInfo")
                    .whereEqualTo("ID",userID)
                    .whereLessThan("growth",100)

            query.get().addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0] // 첫 번째 도큐먼트 가져오기

                    val growth = document.getLong("growth")

                    //**성장도 체크해서 이미지 변경하는 코드 추가**
                    if(growth!!.compareTo(0) == 0){
                        image.setImageResource(R.drawable.ch0)
                    }
                    else{
                        image.setImageResource(R.drawable.ch1)
                    }

                } else {
                    Log.w("HomeFragment", "No documents found")
                }
            }.addOnFailureListener { e ->
                Log.w("HomeFragment", "fetchGrowthDataFail", e)
            }
        }, 200)


    }
}