package com.example.vft

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ReadOnlyActivity : AppCompatActivity(){

    private lateinit var edtTitle:TextView
    private lateinit var edtContent:TextView
    private lateinit var edtCount:TextView
    private lateinit var edtComment:TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var docId: String
    private lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_only)

        edtTitle = findViewById(R.id.edtTitle)
        edtContent = findViewById(R.id.edtContent)
        edtCount = findViewById(R.id.count)
        edtComment = findViewById(R.id.edtComment)

        db = FirebaseFirestore.getInstance()
        docId = intent.getStringExtra("id").toString()
        userID = Firebase.auth.currentUser!!.email.toString() //유저 이메일(아이디)


        //**데이터베이스에서 고민 제목, 내용 가져와서 띄우는 코드**
        fetchDataFromFirestore()

    }

    private fun fetchDataFromFirestore(){
        val docRef = db.collection("troubleList").document(docId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    edtTitle.setText(document.getString("title"))
                    edtContent.setText(document.getString("Content"))
                    edtComment.setText(document.getString("Comment"))
                    edtCount.text = edtContent.text.length.toString() //글자수 표시
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ReadOnlyActivity", "fetchDataFail", exception)
            }
    }

}