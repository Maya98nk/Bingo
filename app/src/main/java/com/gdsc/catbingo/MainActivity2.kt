package com.gdsc.catbingo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import com.gdsc.catbingo.R.id.idet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class MainActivity2 : AppCompatActivity() {
    lateinit var codeEdt: EditText
    lateinit var createCodeBtn: ImageButton
    lateinit var joinCodeBtn: ImageButton
    lateinit var loadingPB: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        joinCodeBtn = findViewById<ImageButton>(R.id.imageButton3)
        createCodeBtn = findViewById<ImageButton>(R.id.imageButton4)
        loadingPB = findViewById<ProgressBar>(R.id.idPBloading)
        codeEdt = findViewById(R.id.idet)

        joinCodeBtn.setOnClickListener {
            val enteredCode = codeEdt.text.toString().trim()

            if (enteredCode.isNotEmpty()) {
                createCodeBtn.visibility = View.GONE
                joinCodeBtn.visibility = View.GONE
                codeEdt.visibility = View.GONE
                loadingPB.visibility = View.VISIBLE

                val gamesRef = FirebaseDatabase.getInstance().reference.child("games")
                gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var codeFound = false
                        for (gameSnapshot in snapshot.children) {
                            val code = gameSnapshot.getValue(String::class.java)
                            if (code.equals(enteredCode, ignoreCase = true)) {
                                codeFound = true
                                startActivity(Intent(this@MainActivity2, MainActivity3::class.java))
                                finish()
                                break
                            }
                        }

                        Log.d("CodeValidation", "Entered Code: $enteredCode, Code Found: $codeFound")

                        if (!codeFound) {
                            createCodeBtn.visibility = View.VISIBLE
                            joinCodeBtn.visibility = View.VISIBLE
                            codeEdt.visibility = View.VISIBLE
                            loadingPB.visibility = View.GONE
                            Toast.makeText(this@MainActivity2, "Invalid Code", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        createCodeBtn.visibility = View.VISIBLE
                        joinCodeBtn.visibility = View.VISIBLE
                        codeEdt.visibility = View.VISIBLE
                        loadingPB.visibility = View.GONE
                        Toast.makeText(this@MainActivity2, "Database Error", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@MainActivity2, "Enter a code", Toast.LENGTH_SHORT).show()
            }
        }




        createCodeBtn.setOnClickListener {
            val code = codeEdt.text.toString().trim()
            if (code != null) {
                createGame(code)
            } else {
                Toast.makeText(this@MainActivity2, "Failed to generate code.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun createGame(code: String) {
        loadingPB.visibility = View.VISIBLE
        FirebaseDatabase.getInstance().reference.child("games").push().setValue(code)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    startActivity(Intent(this@MainActivity2, MainActivity3::class.java))
                    finish()
                } else {
                    Toast.makeText(this@MainActivity2, "Failed to create the game.", Toast.LENGTH_SHORT).show()
                }
                loadingPB.visibility = View.GONE
            }
    }

}
