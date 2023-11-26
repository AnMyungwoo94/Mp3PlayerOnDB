package com.myungwoo.mp3playerondb.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.myungwoo.mp3playerondb.db.DBOpenHelper
import com.myungwoo.mp3playerondb.data.MusicData
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.adapter.MainRecyclerAdapter
import com.myungwoo.mp3playerondb.databinding.ActivityMainBinding
import com.myungwoo.mp3playerondb.data.SubItemData
import com.myungwoo.mp3playerondb.adapter.SubRecyclerAdapter
import com.myungwoo.mp3playerondb.adapter.SubWebviewAdapter

class MainActivity : AppCompatActivity() {
    companion object {
        val REQUEST_CODE = 100
        val DB_NAME = "musicDB2"
        val VERSION = 1
    }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    //데이타베이스 생성
    private val dbOpenHelper by lazy { DBOpenHelper(this, DB_NAME, VERSION) }
    val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val permission_sdk33 = arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO)
    var musicDataList: MutableList<MusicData>? = mutableListOf<MusicData>()
    lateinit var mainRecyclerAdapter: MainRecyclerAdapter
    lateinit var subRecyclerAdapter: SubRecyclerAdapter
    lateinit var subItemDataList: MutableList<SubItemData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //외장메모리 읽기 승인
        var flag = ContextCompat.checkSelfPermission(this, permission[0])
        if (flag == PackageManager.PERMISSION_GRANTED) {
            startProcess()
        } else {
//            //승인요청
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, permission_sdk33, REQUEST_CODE)
            } else{
                ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)

        //메뉴에서 서치객체 찾음
        val searchMenu = menu?.findItem(R.id.menu_search)
        val searchView = searchMenu?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    musicDataList?.clear()
                    dbOpenHelper.selectAllMusicTBL()?.let { musicDataList?.addAll(it) }
                    mainRecyclerAdapter.notifyDataSetChanged()
                } else {
                    musicDataList?.clear()
                    dbOpenHelper.searchMusic(newText)?.let { musicDataList?.addAll(it) }
                    mainRecyclerAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_like -> {
                val musicDataLikeList = dbOpenHelper.selectMusicLike()
                if (musicDataLikeList == null) {
                    Log.e("MainActivity", "musicDataLikeList.size = 0")
                    Toast.makeText(applicationContext, "좋아요 리스트가 없습니다", Toast.LENGTH_SHORT).show()
                } else {
                    musicDataList?.clear()
                    dbOpenHelper.selectMusicLike()?.let { musicDataList?.addAll(it) }
                    mainRecyclerAdapter.notifyDataSetChanged()
                }
            }
            R.id.menu_main -> {
                val musicDataAllList = dbOpenHelper.selectAllMusicTBL()
                if (musicDataAllList!!.size <= 0 || musicDataAllList == null) {
                    Toast.makeText(applicationContext, "모든리스트가 없어요", Toast.LENGTH_SHORT).show()
                } else {
                    musicDataList?.clear()
                    dbOpenHelper.selectAllMusicTBL()?.let { musicDataList?.addAll(it) }
                    mainRecyclerAdapter.notifyDataSetChanged()
                }
            }
            R.id.menu_recode -> {
                val intent = Intent(this, RecordActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startProcess()
            } else {
                Toast.makeText(this, "권한승인을 해야만 앱을 사용할 수 있어요.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startProcess() {
        // 데이타베이스를 조회해서 음악파일이 있다면, 음원정보를 가져와서 데이타베이스 입력했음을 뜻함
        //1. 데이타베이스에서 음원파일을 가져온다.
        var musicDataDBList: MutableList<MusicData>? = mutableListOf<MusicData>()
        musicDataDBList = dbOpenHelper.selectAllMusicTBL()
        Log.e("MainActivity", "musicDataList.size = ${musicDataDBList?.size}")

        if (musicDataDBList == null || musicDataDBList!!.size <= 0) {
            //start 음원정보를 가져옴 **********************************************
            val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
            )
            val cursor = contentResolver.query(musicUri, projection, null, null, null)

            if (cursor!!.count <= 0) {
                Toast.makeText(this, "메모리에 음악파일에 없습니다. 다운받아주세요.", Toast.LENGTH_SHORT).show()
//                finish()
            }
            while (cursor.moveToNext()) {
                val id = cursor.getString(0)
                val title = cursor.getString(1).replace("'", "")
                val artist = cursor.getString(2).replace("'", "")
                val albumId = cursor.getString(3)
                val duration = cursor.getInt(4)
                val musicData = MusicData(id, title, artist, albumId, duration, likes = 0)
                musicDataList?.add(musicData)
            }
            Log.e("MainActivity", "2 musicDataList.size = ${musicDataList?.size}")
            //end 음원정보를 가져옴 **********************************************
            //start 음원정보를 테이블에 insert 해야됨.
            //음악테이블에 모든 음원정보를 insert함
            var size = musicDataList?.size
            if (size != null) {
                for (index in 0..size - 1) {
                    val musicData = musicDataList!!.get(index)
                    dbOpenHelper.insertMusicTBL(musicData)
                }
            }
        } else {
            musicDataList = musicDataDBList
        }

        //1. 액션바대신에 툴바로 대체한다.
        setSupportActionBar(binding.toolbar)

        mainRecyclerAdapter = MainRecyclerAdapter(this, musicDataList!!)
        binding.recyclerView.adapter = mainRecyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        //Adapter와 Subitem_recycler 연결
        subItemDataList = mutableListOf<SubItemData>()
        subItemDataList.add(SubItemData(R.drawable.iv_movieposter1, "https://www.youtube.com/watch?v=0r85vZIzayg"))
        subItemDataList.add(SubItemData(R.drawable.iv_movieposter2, "https://www.youtube.com/watch?v=YIPz1JpaXDc"))
        subItemDataList.add(SubItemData(R.drawable.iv_movieposter3, "https://www.youtube.com/watch?v=LoRwHdN7H1g"))
        subItemDataList.add(SubItemData(R.drawable.iv_movieposter4, "https://www.youtube.com/watch?v=EkRuV-h6Bv0"))

        binding.recyclerViewMain.adapter = SubWebviewAdapter(this,subItemDataList)
        binding.recyclerViewMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        //***************메인 버튼 클릭시 이벤트*******************
        binding.btnYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"))
            startActivity(intent)
        }
        binding.btnAsmr.setOnClickListener {
            val intent = Intent(this, AsmrActivity::class.java)
            startActivity(intent)
        }
    }
}