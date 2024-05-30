package com.varsitycollege.mediaspaceadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.varsitycollege.mediaspaceadmin.data.Colour
import com.varsitycollege.mediaspaceadmin.data.ColourStockAdapter
import com.varsitycollege.mediaspaceadmin.data.Size
import com.varsitycollege.mediaspaceadmin.data.SizeStockAdapter
import com.varsitycollege.mediaspaceadmin.databinding.ActivityManageStockBinding

class ManageStockActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageStockBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val colourList = intent.getSerializableExtra("colours") as ArrayList<Colour>?
        val sizeList = intent.getSerializableExtra("sizes") as ArrayList<Size>?
        val sku = intent.getStringExtra("sku")
        val mode = intent.getStringExtra("mode")

        binding.recyclerViewStock.layoutManager = LinearLayoutManager(applicationContext)
        binding.manageHeader.text = "Managing $mode for $sku"

        //set adapter
        if (colourList != null && sku != null && sizeList != null) {
            if (mode.equals("colours")) {
                val adapter = ColourStockAdapter(colourList, sku)
                binding.recyclerViewStock.adapter = adapter
            }
            else {
                val adapter = SizeStockAdapter(sizeList, sku)
                binding.recyclerViewStock.adapter = adapter
            }
        }

    }
}