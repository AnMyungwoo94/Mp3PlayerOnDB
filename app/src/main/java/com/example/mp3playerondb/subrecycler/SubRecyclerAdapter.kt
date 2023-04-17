package com.example.mp3playerondb.subrecycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mp3playerondb.databinding.SubitemRecyclerBinding


class SubRecyclerAdapter(val mainSubImageList:MutableList<SubItemDataList>):
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