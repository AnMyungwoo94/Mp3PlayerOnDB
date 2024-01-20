package com.myungwoo.mp3playerondb.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.data.AsmrData
import com.myungwoo.mp3playerondb.ui.WebViewActivity

class AsmrWebviewAdapter(var context: Context, private var items: MutableList<AsmrData>) : RecyclerView.Adapter<AsmrWebviewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: View =
            LayoutInflater.from(context).inflate(R.layout.asmr_activity_item, parent, false)
        return Holder(itemView)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item: AsmrData = items[position]
        holder.tvPlayName.setText(item.tvPlayName)
        holder.iv.setImageResource(item.playImage)
        holder.asmrImge.setImageResource(item.AsmrImage)

        holder.iv.setOnClickListener {
            val intent: Intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("title", item.tvPlayName)
            intent.putExtra("titleimage", item.AsmrImage)
            intent.putExtra("imgUrl", item.playImage)
            intent.putExtra("url", item.url)
            context.startActivity(intent)
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPlayName: TextView by lazy { itemView.findViewById(R.id.tvPlayNameAsmrSecond) }
        val iv: ImageView by lazy { itemView.findViewById(R.id.ivPlayAsmrSecond) }
        val asmrImge: ImageView by lazy { itemView.findViewById(R.id.ivAsmrSecond) }
    }
}
