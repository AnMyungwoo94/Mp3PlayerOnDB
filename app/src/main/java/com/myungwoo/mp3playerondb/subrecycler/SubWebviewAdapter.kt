package com.myungwoo.mp3playerondb.subrecycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.asmractivity.AsmrData
import com.myungwoo.mp3playerondb.asmractivity.WebviewActivity

class SubWebviewAdapter(var context : Context, var items : MutableList<SubItemDataList>) : RecyclerView.Adapter<SubWebviewAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.subitem_recycler, parent, false)
        return Holder(itemView)
    }
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        var item: SubItemDataList = items[position]
        holder.ivMainSubImage.setImageResource(item.ivMainSubImage)

        holder.ivMainSubImage.setOnClickListener {
            val intent: Intent = Intent(context, SubWebviewActivity::class.java)
            intent.putExtra("titleimage", item.ivMainSubImage)
            intent.putExtra("url", item.url)
            context.startActivity(intent)
        }
    }
    inner class  Holder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivMainSubImage : ImageView by lazy { itemView.findViewById(R.id.ivMainSubImage) }
        val url: WebView by lazy { itemView.findViewById(R.id.subWebView) }
    }
}