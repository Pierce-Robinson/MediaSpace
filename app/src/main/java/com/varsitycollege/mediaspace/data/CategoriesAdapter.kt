package com.varsitycollege.mediaspace.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.varsitycollege.mediaspace.R
import com.varsitycollege.mediaspace.ui.SingleCategoryFragment

class CategoriesAdapter(private var categories: List<String>, private var viewModel: HomeViewModel) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]

        holder.categoryTitle.text = category

        holder.container.setOnClickListener {

            viewModel.currentCategory.value = category

            //Go to fragment with items from category
            //https://stackoverflow.com/questions/40597840/replace-fragment-from-recycler-adapter
            //Accessed 26 November 2023
            val test = holder.itemView.context as FragmentActivity
            test.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, SingleCategoryFragment()).addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryTitle: MaterialTextView = itemView.findViewById(R.id.categoryTitle)
        val container: CardView = itemView.findViewById(R.id.categoryContainer)
    }

}
