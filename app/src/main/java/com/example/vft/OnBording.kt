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

class OnBording : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding)

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
            startActivity(Intent(this, LogIn::class.java))
            finish()
        }

        signUp.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }
    }


}