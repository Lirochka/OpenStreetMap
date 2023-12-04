package com.example.openstreetmap.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.openstreetmap.R
import com.example.openstreetmap.databinding.FragmentDetailsBinding


class DetailsFragment : Fragment() {
    val args by navArgs<DetailsFragmentArgs>()

    private var _binding: FragmentDetailsBinding? = null
    private val binding: FragmentDetailsBinding
        get() = _binding ?: throw RuntimeException("FragmentDetailsBinding = null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews()
        navigationToPositionFragment()
    }

    private fun bindViews(){
        with(binding){
            imageDetails.setImageResource(args.position.image)
            tvTitle.text = args.position.title
            tvDescription.text = args.position.description
            tvPrice.text = args.position.price
        }
    }
    private fun navigationToPositionFragment(){
        binding.topAppBarToPosition.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_positionFragment)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}