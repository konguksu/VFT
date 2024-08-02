package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class DashBoardFragment : Fragment() {

    private lateinit var adapter: IllustAdapter
    private val illustList = arrayListOf<IllustItem>()

    private lateinit var salmonProg: ProgressBar
    private lateinit var totalSalmon: TextView
    private lateinit var honeyProg: ProgressBar
    private lateinit var totalHoney: TextView
    private lateinit var listView: ListView
    private lateinit var text:TextView

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
        text = view.findViewById(R.id.text)

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

        //일러스트 클릭 시 통계-아카이빙으로 이동
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = illustList[position]
            val intent = Intent(requireContext(), ArchivingActivity::class.java)
            intent.putExtra("selectedItem",selectedItem)
            startActivity(intent)
        }

        //성장 조건 충족 시 리스트에 이미지 추가
        fetchUnlockFromGrowthDB()
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

    //데이터베이스에서 해금 기록 불러오기
    private fun fetchUnlockFromGrowthDB() {
        db.collection("growthInfo")
                .whereEqualTo("ID",userID).whereEqualTo("growth",100) //유저 아이디 일치, 성장도 100
                .orderBy("endDate",Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot: QuerySnapshot?, exception ->
                    if (exception != null) {
                        Log.w("DashBoardFragment", "fetchDataFail", exception)
                    }

                    if (snapshot != null) {
                        illustList.clear()
                        text.visibility = View.INVISIBLE
                        for ((count, document) in snapshot.documents.withIndex()) {
                            if(document.getTimestamp("startDate")!=null&&document.getTimestamp("endDate")!=null){
                                val startDate = document.getTimestamp("startDate")!!
                                val endDate = document.getTimestamp("endDate")!!
                                when(count % 4){
                                    0 -> illustList.add(IllustItem(R.drawable.ch1,startDate,endDate))
                                    1 -> illustList.add(IllustItem(R.drawable.ch0,startDate,endDate))
                                    2 -> illustList.add(IllustItem(R.drawable.ch1,startDate,endDate))
                                    3 -> illustList.add(IllustItem(R.drawable.ch0,startDate,endDate))
                                }
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
    }
}