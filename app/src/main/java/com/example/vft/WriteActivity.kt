package com.example.vft

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class WriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

        val exitBtn = findViewById<Button>(R.id.exitBtn)
        val edtTitle = findViewById<EditText>(R.id.edtTitle)
        val edtContent = findViewById<EditText>(R.id.edtContent)
        val count = findViewById<TextView>(R.id.count)
        val finBtn = findViewById<Button>(R.id.finBtn)

        exitBtn.setOnClickListener {
            val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_write_quit, null)

            val dlg = AlertDialog.Builder(this).setView(dlgView)

            val alertDialog = dlg.create()
            alertDialog.show()

            val yesBtn: Button = dlgView.findViewById(R.id.yesBtn)
            val noBtn: Button = dlgView.findViewById(R.id.noBtn)

            yesBtn.setOnClickListener {
                // 메인 화면으로 이동
                startActivity(Intent(this, MainScreenActivity::class.java))
                finish()
            }

            noBtn.setOnClickListener {
                alertDialog.dismiss()
            }

        }

        //제목 입력
        edtTitle.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                //글자수제한 코드 추가
            }
        })

        //내용 입력
        edtContent.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                //글자수제한 코드 추가
            }
        })

        finBtn.setOnClickListener {
            val dlgView = LayoutInflater.from(this).inflate(R.layout.dialog_write_finish, null)

            val dlg = AlertDialog.Builder(this).setView(dlgView)

            val alertDialog = dlg.create()
            alertDialog.show()

            val continueBtn: Button = dlgView.findViewById(R.id.continueBtn)
            val quitBtn: Button = dlgView.findViewById(R.id.quitBtn)

            continueBtn.setOnClickListener {
                alertDialog.dismiss()
            }

            quitBtn.setOnClickListener {
                // 연어 주기

                // 메인 화면으로 이동
                startActivity(Intent(this, MainScreenActivity::class.java))
                finish()
            }
        }
    }
}
