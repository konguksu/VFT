package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OnBordingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val logo = findViewById<ImageView>(R.id.logo)
        val logIn = findViewById<Button>(R.id.logIn)
        val signUp = findViewById<TextView>(R.id.signUp)
        val moveUp = AnimationUtils.loadAnimation(this, R.anim.logo_move_up)
        moveUp.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                logIn.visibility = View.VISIBLE
                signUp.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        logo.startAnimation(moveUp)

        logIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        signUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    //이미 로그인을 했는지 체크
    override fun onStart() {
        super.onStart()
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        //로그인을 한 상태면 바로 메인 화면으로
        if (currentUser != null) {
            startActivity(Intent(this, MainScreenActivity::class.java))
        }
    }


}