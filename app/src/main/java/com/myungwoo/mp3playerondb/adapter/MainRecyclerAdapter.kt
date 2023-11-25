package com.myungwoo.mp3playerondb.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.myungwoo.mp3playerondb.*
import com.myungwoo.mp3playerondb.databinding.ItemRecyclerBinding
import com.myungwoo.mp3playerondb.db.DBOpenHelper
import com.myungwoo.mp3playerondb.data.MusicData
import com.myungwoo.mp3playerondb.view.MainActivity
import com.myungwoo.mp3playerondb.view.PlayActivity
import java.text.SimpleDateFormat

class MainRecyclerAdapter(val context:Context, val musicList:MutableList<MusicData>):
    RecyclerView.Adapter<MainRecyclerAdapter.CustomViewHolder>() {
    val ALBUM_IMAGE_SIZE = 90
    var like = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomViewHolder(binding)
    }

    override fun getItemCount(): Int = musicList.size

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = holder.binding
        // 이미지, artist, title, duration binding
        val bitmap = musicList.get(position).getAlbumBitmap(context, ALBUM_IMAGE_SIZE)
        if(bitmap != null){
            binding.ivAlbumArt.setImageBitmap(bitmap)
        }else{
            binding.ivAlbumArt.setImageResource(R.drawable.music)
        }
        binding.tvArtist.text = musicList.get(position).artist
        binding.tvTitle.text = musicList.get(position).title
        binding.tvDuration.text = SimpleDateFormat("mm:ss").format(musicList.get(position).duration)
        when(musicList.get(position).likes){
            0-> binding.ivItemLike.setImageResource(R.drawable.favorite_24)
            1-> binding.ivItemLike.setImageResource(R.drawable.favorite_like_24)
        }

        // 아이템항목 클릭 시 PlayActivity MusicData 전달
        binding.root.setOnClickListener {
            val intent = Intent(binding.root.context, PlayActivity::class.java)
            val parcelableList : ArrayList<Parcelable>? = musicList as ArrayList<Parcelable>
            intent.putExtra("parcelableList",parcelableList)
            intent.putExtra("position",position)
            context.startActivity(intent)
            }

        // 삭제 버튼 구현
        binding.root.setOnLongClickListener {
            musicList.removeAt(position)
            notifyDataSetChanged()
            true
        }

        //좋아요 버튼 구현
        binding.ivItemLike.setOnClickListener {
            when(musicList.get(position).likes){
                0->{
                    musicList.get(position).likes = 1
                    binding.ivItemLike.setImageResource(R.drawable.favorite_like_24)
                }
                1->{
                    musicList.get(position).likes = 0
                    binding.ivItemLike.setImageResource(R.drawable.favorite_24)
                }
            }
        val db = DBOpenHelper(context, MainActivity.DB_NAME, MainActivity.VERSION)
        val errorFlag = db.updateLike(musicList.get(position))
            if(errorFlag){
                Toast.makeText(context, "updateLike 실패", Toast.LENGTH_SHORT).show()
            }else{
                this.notifyDataSetChanged()
            }
        }
    }
    inner class CustomViewHolder(val binding:ItemRecyclerBinding):RecyclerView.ViewHolder(binding.root)
}