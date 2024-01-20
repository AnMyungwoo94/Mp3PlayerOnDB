package com.myungwoo.mp3playerondb.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.data.SubItemData
import com.myungwoo.mp3playerondb.ui.SubWebViewActivity

class SubWebviewAdapter(
    var context: Context, private var items: MutableList<SubItemData>
) : RecyclerView.Adapter<SubWebviewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.subitem_recycler, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item: SubItemData = items[position]
        holder.ivMainSubImage.setImageResource(item.ivMainSubImage)

        holder.ivMainSubImage.setOnClickListener {
            val intent = Intent(context, SubWebViewActivity::class.java)
            intent.putExtra("titleimage", item.ivMainSubImage)
            intent.putExtra("url", item.url)
            context.startActivity(intent)
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivMainSubImage: ImageView by lazy { itemView.findViewById(R.id.ivMainSubImage) }
    }
}