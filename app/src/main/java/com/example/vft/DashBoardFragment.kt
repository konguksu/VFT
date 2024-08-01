package com.example.vft

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashBoardFragment : Fragment() {

    private lateinit var adapter: IllustAdapter
    private val illustList = arrayListOf<IllustItem>()

    private lateinit var salmonProg: ProgressBar
    private lateinit var totalSalmon: TextView
    private lateinit var honeyProg: ProgressBar
    private lateinit var totalHoney: TextView
    private lateinit var listView: ListView

    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nickName: TextView = view.findViewById(R.id.nickName)
        salmonProg = view.findViewById(R.id.salmonProg)
        totalSalmon= view.findViewById(R.id.totalSalmon)
        honeyProg= view.findViewById(R.id.honeyProg)
        totalHoney= view.findViewById(R.id.totalHoney)
        listView= view.findViewById(R.id.listView)

        adapter = IllustAdapter(requireContext(), illustList)
        listView.adapter = adapter

        db = Firebase.firestore
        userID = Firebase.auth.currentUser!!.email.toString() //유저 이메일(아이디)

        //데이터베이스에서 닉네임 가져와서 띄우기
        db.collection("userInfo").document(userID).get().addOnSuccessListener {document ->
            if (document != null) {
                nickName.text = document.getString("nickname").toString()
            }
        }.addOnFailureListener { exception ->
            Log.w("DashBoardActivity", "fetchDataFail", exception)
        }

        //연어, 꿀 개수 표시
        fetchNumFromFirestore()

        //**일러스트 클릭 시 통계화면_아카이빙으로 이동**

        //**성장 조건 충족 시 데이터베이스에서 일러스트 가져와서 리스트에 이미지 추가**
    }

    //데이터베이스에서 해당 유저가 작성한 고민, 코멘트 개수 불러오기
    private fun fetchNumFromFirestore() {
        //연어 프로그래스바 관련 코드
        db.collection("troubleList")
                .whereEqualTo("userID",userID) //유저 아이디 일치, 고민
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        Log.w("DashBoardActivity", "fetchDataFail", exception)
                    }

                    if (snapshot != null) {
                        totalSalmon.text = snapshot.documents.size.toString()
                    }
                }
        //꿀 프로그래스바 관련 코드
        db.collection("troubleList")
                .whereEqualTo("userID",userID).whereNotEqualTo("Comment","") //유저 아이디 일치, 코멘트 있는 고민
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        Log.w("DashBoardActivity", "fetchDataFail", exception)
                    }

                    if (snapshot != null) {
                        totalHoney.text = snapshot.documents.size.toString()
                    }
                }
    }
}