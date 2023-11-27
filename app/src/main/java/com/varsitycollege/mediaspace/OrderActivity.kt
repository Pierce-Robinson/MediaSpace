package com.varsitycollege.mediaspace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val orderNumText = findViewById<TextView>(R.id.orderNo)
        orderNumText.text = "Order No (Use as reference): " + intent.getStringExtra("orderNo")

        val goBack = findViewById<MaterialButton>(R.id.GoBackButton)
        goBack.setOnClickListener {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}