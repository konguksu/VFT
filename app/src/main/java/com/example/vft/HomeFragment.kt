package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment

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
}