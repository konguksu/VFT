package com.example.vft

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainscreen)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when(item.itemId) {
                R.id.navigation_dashboard -> selectedFragment = DashBoardFragment()
                R.id.navigation_home -> selectedFragment = HomeFragment()
                R.id.navigation_mypage -> selectedFragment = MyPageFragment()
            }
            if (selectedFragment != null)
                supportFragmentManager.beginTransaction().replace(R.id.container, selectedFragment).commit()
            true
        }
        //기본 페이지
        bottomNavigationView.selectedItemId = R.id.navigation_home
    }
}