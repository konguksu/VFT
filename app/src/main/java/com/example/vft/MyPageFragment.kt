package com.example.vft

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
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

        //함께한지 00일

        // 임시로 날짜 설정 (Firebase에서 데이터를 가져와야 함)
        val worryDays = setOf(
            CalendarDay.from(2024, 8, 1),
            CalendarDay.from(2024, 8, 5)
        )
        val commentDays = setOf(
            CalendarDay.from(2024, 8, 2),
            CalendarDay.from(2024, 8, 6)
        )
        val bothDays = setOf(
            CalendarDay.from(2024, 8, 8)
        )

        // 각 색상별 drawable 리소스 가져오기
        val worryDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.calendar_circle_salmon)
        val commentDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.calendar_circle_honey)
        val bothDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.calendar_circle_both)

        // 오늘 날짜 표시할 색상
        val todayTextColor = ContextCompat.getColor(requireContext(), R.color.salmon_900)
        val todayDecorator = TodayDecorator(todayTextColor)

        // 데코레이터 추가
        calendar.addDecorator(EventDecorator(worryDrawable, worryDays))
        calendar.addDecorator(EventDecorator(commentDrawable, commentDays))
        calendar.addDecorator(EventDecorator(bothDrawable, bothDays))
        calendar.addDecorator(todayDecorator)

        //로그아웃 버튼
        logout.setOnClickListener {
            val dlgView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
            val dlg = AlertDialog.Builder(requireContext()).setView(dlgView)

            val alertDialog = dlg.create()
            alertDialog.show()

            val yesBtn: Button = dlgView.findViewById(R.id.yes)
            val noBtn: Button = dlgView.findViewById(R.id.no)

            yesBtn.setOnClickListener {
                alertDialog.dismiss()
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(requireContext(), OnBordingActivity::class.java)
                startActivity(intent)

                requireActivity().finish()

                Toast.makeText(requireContext(), "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            }

            noBtn.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }

    class EventDecorator(
        private val drawable: Drawable?,
        private val dates: Set<CalendarDay>
    ) : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            drawable?.let { view.setBackgroundDrawable(it) }
        }
    }

    private class TodayDecorator(private val textColor: Int) : DayViewDecorator {

        private val today = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == today
        }

        override fun decorate(view: DayViewFacade) {
            // 오늘 날짜의 텍스트 색상을 변경
            view.addSpan(ForegroundColorSpan(textColor))
            view.addSpan(StyleSpan(Typeface.BOLD))
        }
    }
}
