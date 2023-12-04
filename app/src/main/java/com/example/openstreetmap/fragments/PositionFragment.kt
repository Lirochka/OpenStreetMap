package com.example.openstreetmap.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openstreetmap.R
import com.example.openstreetmap.adapter.CategoryAdapter
import com.example.openstreetmap.adapter.PositionAdapter
import com.example.openstreetmap.databinding.FragmentCategoryBinding
import com.example.openstreetmap.databinding.FragmentPositionBinding
import com.example.openstreetmap.model.Category
import com.example.openstreetmap.model.Position

class PositionFragment : Fragment() {

    private val adapter = PositionAdapter()
    private val listCategory = listOf(
        Position(R.drawable.pizza_2, "Пицца 4 сыра", "пицца соус (томаты базилик орегано чеснок), моцарелла для пиццы, смесь сыров", "569 ₽" ),
        Position(R.drawable.rolls_2, "Унаги маки", "соус \"Унаги\", рис, нори, огурцы свежие, угорь копченый, кунжут", "239 ₽"),
        Position(R.drawable.soup_2, "Том Ям", "сливки, лемонграсс, кокосовое молоко, чили перец, чеснок, креветки, рис", "389 ₽"),
        Position(R.drawable.sushi_2, "Спайс эби", "рис, нори, креветки, соус \"Спайс\"", "119 ₽"),
        Position(R.drawable.wok_2, "Тяхан с курицей", "масло растительное, грудка куриная, морковь, лук репчатый, перец болгарский, рис, соус \"Чесночный\", кунжут" ,"309 ₽"))

    private var _binding: FragmentPositionBinding? = null
    private val binding: FragmentPositionBinding
        get() = _binding ?: throw RuntimeException("FragmentPositionBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPositionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        navigationToDetails()
        navigationToCategory()
    }
    private fun navigationToDetails(){
        adapter.positionClickListener = object : PositionAdapter.OnPositionClickListener {
            override fun onPositionClick(position: Position) {
                findNavController().navigate(
                    PositionFragmentDirections.actionPositionFragmentToDetailsFragment(position)
                )
            }
        }
    }
    private fun initRecyclerView() {
        binding.apply {
            rcPosition.layoutManager = LinearLayoutManager(requireContext())
            rcPosition.adapter = adapter

            adapter.positionList.clear()

            for (i in 0 until 20) {
                val (image, title, description, price) = listCategory[i % listCategory.size]
                val position = Position(image, title, description, price)
                adapter.positionList.add(position)
            }
            adapter.notifyDataSetChanged()
        }
    }
    private fun navigationToCategory(){
        binding.topAppBarToCategory.setOnClickListener {
            findNavController().navigate(R.id.action_positionFragment_to_categoryFragment)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}