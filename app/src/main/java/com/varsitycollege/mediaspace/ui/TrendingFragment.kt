package com.varsitycollege.mediaspace.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.data.Product
import com.varsitycollege.mediaspace.data.ProductAdapter
import com.varsitycollege.mediaspace.databinding.FragmentTrendingBinding

class TrendingFragment : Fragment() {
    private lateinit var productList: ArrayList<Product>
    private lateinit var productRecyclerView: RecyclerView
    private var _binding: FragmentTrendingBinding? = null
    private lateinit var adapter: ProductAdapter
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        _binding = FragmentTrendingBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getProductTrending(){
        productRecyclerView = binding.recyclerViewTrending
        productList = arrayListOf()

        productRecyclerView.layoutManager = LinearLayoutManager(context)


        //TODO: get all products and add it to the adapter
    }



}