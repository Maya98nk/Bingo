package com.gdsc.catbingo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class aboutPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page)

        val tutPage1 = findViewById<ImageButton>(R.id.tutPage)
        tutPage1.setOnClickListener {
            val intent = Intent(this, tutPage::class.java)
            startActivity(intent)
        }
    }

}