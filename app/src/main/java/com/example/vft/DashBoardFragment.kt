package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class DashBoardFragment : Fragment() {

    private lateinit var adapter: IllustAdapter
    private val illustList = arrayListOf<IllustItem>()

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
        val salmonProg: ProgressBar = view.findViewById(R.id.salmonProg)
        val totalSalmon: TextView = view.findViewById(R.id.totalSalmon)
        val honeyProg: ProgressBar = view.findViewById(R.id.honeyProg)
        val totalHoney: TextView = view.findViewById(R.id.totalHoney)
        val listView: ListView = view.findViewById(R.id.listView)

        adapter = IllustAdapter(requireContext(), illustList)
        listView.adapter = adapter

        //**데이터베이스에서 닉네임 가져와서 띄우기**

        //**연어 프로그래스바 관련 코드**

        //**꿀 프로그래스바 관련 코드**

        //**일러스트 클릭 시 통계-아카이빙으로 이동**
//        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            val selectedItem = illustList[position]
//            val intent = Intent(requireContext(), ArchivingActivity::class.java)
//            startActivity(intent)
//        }

        //**성장 조건 충족 시 데이터베이스에서 일러스트 가져와서 리스트에 이미지 추가**
    }
}