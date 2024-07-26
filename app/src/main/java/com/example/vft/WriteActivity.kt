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

        edtTitle.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(editable: Editable) {
                if (isEditing) return

                isEditing = true
                val maxByteLength = 100
                val currentText = editable.toString()
                val byteArray = currentText.toByteArray(Charsets.UTF_8)

                if (byteArray.size > maxByteLength) {
                    Toast.makeText(applicationContext, "입력 범위를 넘었습니다.", Toast.LENGTH_SHORT).show()

                    // 바이트 수를 제한하는 텍스트 자르기
                    var truncatedText = currentText
                    while (truncatedText.toByteArray(Charsets.UTF_8).size > maxByteLength) {
                        truncatedText = truncatedText.dropLast(1)
                    }

                    // 잘린 텍스트를 설정하고 커서를 끝으로 이동
                    edtTitle.setText(truncatedText)
                    edtTitle.setSelection(truncatedText.length)
                } else {
                    edtTitle.setText(currentText)
                    edtTitle.setSelection(currentText.length)
                }

                isEditing = false
            }
        })

        edtContent.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(editable: Editable) {
                if (isEditing) return

                isEditing = true
                val maxByteLength = 800
                val currentText = editable.toString()
                val byteArray = currentText.toByteArray(Charsets.UTF_8)

                if (byteArray.size > maxByteLength) {
                    Toast.makeText(applicationContext, "입력 범위를 넘었습니다.", Toast.LENGTH_SHORT).show()

                    // 바이트 수를 제한하는 텍스트 자르기
                    var truncatedText = currentText
                    while (truncatedText.toByteArray(Charsets.UTF_8).size > maxByteLength) {
                        truncatedText = truncatedText.dropLast(1)
                    }

                    // 잘린 텍스트를 설정하고 커서를 끝으로 이동
                    edtContent.setText(truncatedText)
                    edtContent.setSelection(truncatedText.length)
                } else {
                    edtContent.setText(currentText)
                    edtContent.setSelection(currentText.length)
                }

                // 바이트 길이를 화면에 표시
                count.text = byteArray.size.toString()

                isEditing = false
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
