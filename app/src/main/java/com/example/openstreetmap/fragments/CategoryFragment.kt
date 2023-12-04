package com.example.openstreetmap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.openstreetmap.R
import com.example.openstreetmap.adapter.CategoryAdapter
import com.example.openstreetmap.databinding.FragmentCategoryBinding
import com.example.openstreetmap.model.Category


class CategoryFragment : Fragment() {

    private val adapter = CategoryAdapter()
    private val listCategory = listOf(
       Pair(R.drawable.pizza, "Пицца"),
       Pair(R.drawable.rolls, "Роллы"),
       Pair(R.drawable.soup, "Супы"),
       Pair(R.drawable.sushi, "Суши"),
       Pair(R.drawable.wok, "Wok"))

    private var _binding: FragmentCategoryBinding? = null
    private val binding: FragmentCategoryBinding
        get() = _binding ?: throw RuntimeException("FragmentCategoryBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigation()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.apply {
            rcCategory.layoutManager = GridLayoutManager(requireContext(), 2)
            rcCategory.adapter = adapter

            adapter.categoryList.clear()

            for (i in 0 until 20) {
                val (image, title) = listCategory[i % listCategory.size]
                val category = Category(image, title)
                adapter.categoryList.add(category)
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun navigation(){
        binding.topAppBarToMap.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_mapFragment)
        }
        adapter.categoryClickListener = object : CategoryAdapter.OnCategoryClickListener {
            override fun onCategoryClick() {
                findNavController().navigate(R.id.action_categoryFragment_to_positionFragment)
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}