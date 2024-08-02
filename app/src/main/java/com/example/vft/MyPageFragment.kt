package com.example.vft

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class MyPageFragment : Fragment() {

    private lateinit var profileImg: ImageView
    private lateinit var day: TextView
    private lateinit var logout: Button
    private lateinit var calendar: MaterialCalendarView
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nickName: TextView = view.findViewById(R.id.nickName)
        profileImg = view.findViewById(R.id.profileImg)
        day = view.findViewById(R.id.day)
        logout = view.findViewById(R.id.logout)
        calendar = view.findViewById(R.id.calendar)

        db = Firebase.firestore
        userID = Firebase.auth.currentUser!!.email.toString()

        // 데이터베이스에서 닉네임 가져와서 띄우기
        db.collection("userInfo").document(userID).get().addOnSuccessListener { document ->
            if (document != null) {
                nickName.text = document.getString("nickname").toString()
            }
        }.addOnFailureListener { exception ->
            Log.w("MyPageFragment", "fetchDataFail", exception)
        }

        //프로필 이미지 설정
        //날짜 설정
        //로그아웃 버튼
        //캘린더 커스터마이징
    }
}
