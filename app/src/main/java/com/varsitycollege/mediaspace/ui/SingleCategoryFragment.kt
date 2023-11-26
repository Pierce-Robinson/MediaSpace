package com.varsitycollege.mediaspace.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.data.HomeViewModel
import com.varsitycollege.mediaspace.databinding.FragmentCategoriesBinding
import com.varsitycollege.mediaspace.databinding.FragmentSingleCategoryBinding

class SingleCategoryFragment : Fragment() {

    private var _binding: FragmentSingleCategoryBinding? = null
    private val model: HomeViewModel by activityViewModels()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingleCategoryBinding.inflate(inflater, container, false)

        Toast.makeText(this@SingleCategoryFragment.context, "${model.currentCategory.value}", Toast.LENGTH_LONG).show()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}