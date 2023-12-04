package com.example.openstreetmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.openstreetmap.R
import com.example.openstreetmap.databinding.ItemPositionBinding
import com.example.openstreetmap.model.Position

class PositionAdapter : RecyclerView.Adapter<PositionAdapter.PositionHolder>() {

    var positionClickListener: OnPositionClickListener? = null
    val positionList = ArrayList<Position>()
    class PositionHolder(item: View) : RecyclerView.ViewHolder(item) {
        val binding = ItemPositionBinding.bind(item)
        fun bind(position: Position) = with(binding) {
            imageCategory.setImageResource(position.image)
            tvTitle.text = position.title
            tvDescription.text = position.description
            tvPrice.text = position.price
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_position, parent, false)
        return PositionHolder(view)
    }
    override fun onBindViewHolder(holder: PositionHolder, position: Int) {
        val currentPosition = (positionList[position])
        holder.bind(currentPosition)
        holder.binding.cardPosition.setOnClickListener {
            positionClickListener?.onPositionClick(currentPosition)
        }
    }
    override fun getItemCount(): Int {
      return positionList.size
    }
    interface OnPositionClickListener {
        fun onPositionClick(position: Position)
    }
}