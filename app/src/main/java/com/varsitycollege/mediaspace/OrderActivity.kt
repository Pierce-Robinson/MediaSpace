package com.varsitycollege.mediaspace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val orderNumText = findViewById<TextView>(R.id.orderNo)
        orderNumText.text = "Order No (Use as reference): " + intent.getStringExtra("orderNo")

    }
}