package com.example.openstreetmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openstreetmap.R
import com.example.openstreetmap.databinding.ItemCategoryBinding
import com.example.openstreetmap.model.Category

class CategoryAdapter() :
    RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    var categoryClickListener: OnCategoryClickListener? = null
    val categoryList = ArrayList<Category>()
    class CategoryHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemCategoryBinding.bind(item)
        fun bind(category: Category) = with(binding) {
            imageCategory.setImageResource(category.image)
            tvTitle.text = category.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryHolder(view)
    }
    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
       holder.bind(categoryList[position])
        holder.binding.cardCategory.setOnClickListener {
            categoryClickListener?.onCategoryClick()
        }
    }
    override fun getItemCount(): Int {
      return categoryList.size
    }
    interface OnCategoryClickListener {
        fun onCategoryClick()
    }
}