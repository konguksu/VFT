package com.example.vft

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
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
import androidx.annotation.RequiresApi
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
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.random.Random

var img = R.drawable.ch0
var isImgSet =false

class MyPageFragment : Fragment() {

    private lateinit var profileImg: ImageView
    private lateinit var day: TextView
    private lateinit var logout: Button
    private lateinit var calendar: MaterialCalendarView
    private lateinit var db: FirebaseFirestore
    private lateinit var userID: String

    private var worryDays: MutableSet<CalendarDay> = mutableSetOf()
    private var commentDays: MutableSet<CalendarDay> = mutableSetOf()
    private var bothDays: MutableSet<CalendarDay> = mutableSetOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        if(!isImgSet){
            val drawableResources = arrayOf(
                R.drawable.ic_profile1,
                R.drawable.ic_profile2,
                R.drawable.ic_profile3,
                R.drawable.ic_profile4
            )
            val randomIndex = Random.nextInt(drawableResources.size)
            img = drawableResources[randomIndex]
            isImgSet = true
        }
        profileImg.setImageResource(img)

        //함께한지 00일
        db.collection("userInfo").document(userID).get().addOnSuccessListener { document ->
            if (document != null) {
                val currentDate = Date(System.currentTimeMillis())
                val joinDate = document.getTimestamp("joinDate")!!.toDate()

                // Date 객체를 LocalDate 객체로 변환
                val joinLocalDate = joinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                val currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                val daysBetween = ChronoUnit.DAYS.between(joinLocalDate, currentLocalDate)

                day.text = daysBetween.toString()
            }
        }.addOnFailureListener { exception ->
            Log.w("MyPageFragment", "fetchDataFail", exception)
        }

        // Firebase에서 데이터를 가져와서 캘린더에 고민/코멘트 기록 날짜 표시
        fetchDate()


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

    //고민, 코멘트 기록한 날짜 db에서 가져오기
    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchDate(){
        val query = db.collection("troubleList")
            .whereEqualTo("userID",userID)

        query.get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                for(document in querySnapshot.documents){
                    val writeTimestamp = document.getTimestamp("writeDate")
                    val commentTimestamp = document.getTimestamp("commentDate")
                    //우선 고민 기록한 날짜 모두 worryDays에 추가
                    if (writeTimestamp != null) {
                        val writeDate = writeTimestamp.toDate()

                        // Date 객체를 LocalDate 객체로 변환
                        val localDate = writeDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                        // LocalDate 객체를 CalendarDay 객체로 변환
                        val calendarDay = CalendarDay.from(
                            localDate.year,
                            localDate.monthValue,
                            localDate.dayOfMonth
                        )

                        worryDays.add(calendarDay) // worryDays에 CalendarDay 객체 추가
                    }
                    //코멘트가 작성되어 있으면 코멘트 작성한 날짜 확인
                    if(document.getString("Comment") != "" && commentTimestamp != null){
                        val commentDate = commentTimestamp.toDate()

                        // Date 객체를 LocalDate 객체로 변환
                        val localDate = commentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                        // LocalDate 객체를 CalendarDay 객체로 변환
                        val calendarDay = CalendarDay.from(
                            localDate.year,
                            localDate.monthValue,
                            localDate.dayOfMonth
                        )
                        //코멘트 작성한 날짜에 고민도 작성했으면
                        if (worryDays.contains(calendarDay)){
                            worryDays.remove(calendarDay)
                            bothDays.add(calendarDay)
                        }
                        //코멘트 작성한 날짜에 고민은 작성하지 않았으면
                        else{
                            commentDays.add(calendarDay)
                        }

                    }
                }
                //캘린더에 표시
                setCalender()

            } else {
                Log.w("MyPageFragment", "No documents found")
            }
        }.addOnFailureListener { e ->
            Log.w("MyPageFragment", "fetchGrowthDataFail", e)
        }
    }

    //캘린더에 표시
    private fun setCalender(){
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
