package com.myungwoo.mp3playerondb.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myungwoo.mp3playerondb.databinding.SubitemRecyclerBinding
import com.myungwoo.mp3playerondb.data.SubItemData


class SubRecyclerAdapter(private val mainSubImageList:MutableList<SubItemData>):
    RecyclerView.Adapter<SubRecyclerAdapter.CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = SubitemRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomViewHolder(binding)
    }
    override fun getItemCount(): Int = mainSubImageList.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        binding.ivMainSubImage.setImageResource(mainSubImageList.get(position).ivMainSubImage)
    }
    inner class CustomViewHolder(val binding:SubitemRecyclerBinding):RecyclerView.ViewHolder(binding.root)
}