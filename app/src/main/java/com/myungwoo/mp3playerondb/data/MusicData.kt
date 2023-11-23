package com.myungwoo.mp3playerondb.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import com.bumptech.glide.Glide
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
class MusicData( var id: String, var title: String?, var artist: String?, var albumId: String?, var duration: Int?, var likes: Int?) : Parcelable {
    companion object : Parceler<MusicData> {
        override fun create(parcel: Parcel): MusicData {
           return  MusicData(parcel)
        }
        override fun MusicData.write(parcel: Parcel, flags: Int) {
           parcel.writeString(id)
           parcel.writeString(title)
           parcel.writeString(artist)
           parcel.writeString(albumId)
           parcel.writeInt(duration!!)
           parcel.writeInt(likes!!)
        }
    }//end of compainion object
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
    )

    // 음악 id를 통해서 음악 파일 Uri를 가져 오는 함수
    fun getMusicUri():Uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.id)

    fun getAlbumUri():Uri = Uri.parse("content://media/external/audio/albumart/${this.albumId}")

    fun getAlbumBitmap(context: Context, albumSize: Int): Bitmap? {
        val albumUri = getAlbumUri()

        return try {
            if (albumUri != null) {
                val requestBuilder = Glide.with(context)
                    .asBitmap()
                    .load(albumUri)

                val bitmap = requestBuilder.submit().get()
                // 크기 조정 등의 추가적인 처리
                bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MusicData", e.toString())
            null
        }
    }
}
