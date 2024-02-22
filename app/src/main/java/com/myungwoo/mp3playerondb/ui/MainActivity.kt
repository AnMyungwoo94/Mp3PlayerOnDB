package com.myungwoo.mp3playerondb.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.myungwoo.mp3playerondb.db.DBOpenHelper
import com.myungwoo.mp3playerondb.data.MusicData
import com.myungwoo.mp3playerondb.R
import com.myungwoo.mp3playerondb.ui.adapter.MainRecyclerAdapter
import com.myungwoo.mp3playerondb.databinding.ActivityMainBinding
import com.myungwoo.mp3playerondb.data.SubItemData
import com.myungwoo.mp3playerondb.ui.adapter.SubWebviewAdapter
import com.myungwoo.mp3playerondb.util.showSnackbar
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE = 100
        const val DB_NAME = "musicDB2"
        const val VERSION = 1
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var mainRecyclerAdapter: MainRecyclerAdapter
    private lateinit var subItemDataList: MutableList<SubItemData>
    private val dbOpenHelper by lazy { DBOpenHelper(this, DB_NAME, VERSION) }
    private var musicDataList: MutableList<MusicData>? = mutableListOf()

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isDialogShown = prefs.getBoolean("isDialogShown", false)

        if (!isDialogShown) {
            val dialogFragment = DialogFragment()
            dialogFragment.show(supportFragmentManager, "dialogFragment")
            prefs.edit().putBoolean("isDialogShown", true).apply()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setPermission()
    }

    private fun setPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val flag = ContextCompat.checkSelfPermission(this, permission)
        if (flag == PackageManager.PERMISSION_GRANTED) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_item, menu)

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
                    binding.clMain.showSnackbar(R.string.main_like_list)
                } else {
                    musicDataList?.clear()
                    dbOpenHelper.selectMusicLike()?.let { musicDataList?.addAll(it) }
                    mainRecyclerAdapter.notifyDataSetChanged()
                }
            }

            R.id.menu_main -> {
                try {
                    val musicDataAllList = dbOpenHelper.selectAllMusicTBL()
                    if (musicDataAllList?.size == null) {
                        binding.clMain.showSnackbar(R.string.main_not_all_list)
                    } else {
                        musicDataList?.clear()
                        dbOpenHelper.selectAllMusicTBL()?.let { musicDataList?.addAll(it) }
                        mainRecyclerAdapter.notifyDataSetChanged()
                    }
                } catch (e: IOException) {
                    Log.e("List error", e.toString())
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
                binding.clMain.showSnackbar(R.string.main_permission)
                showPermissionSettingDialog()
            }
        }
    }

    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.main_permission)
            .setPositiveButton(R.string.record_setting) { _, _ ->
                navigateToAppSetting()
            }.setNegativeButton(R.string.record_cancel) { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }

    private fun navigateToAppSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun startProcess() {
        val musicDataDBList: MutableList<MusicData>? = dbOpenHelper.selectAllMusicTBL()
        if ((musicDataDBList == null) || (musicDataDBList.size <= 0)) {
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
                binding.clMain.showSnackbar(R.string.main_no_music)
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
            val size = musicDataList?.size
            if (size != null) {
                for (index in 0 until size) {
                    val musicData = musicDataList!![index]
                    dbOpenHelper.insertMusicTBL(musicData)
                }
            }
        } else {
            musicDataList = musicDataDBList
        }

        setSupportActionBar(binding.toolbarMain)
        mainRecyclerAdapter = MainRecyclerAdapter(this, musicDataList!!)
        binding.rvMainMusicList.adapter = mainRecyclerAdapter
        binding.rvMainMusicList.layoutManager = LinearLayoutManager(this)

        subItemDataList = mutableListOf()
        subItemDataList.add(SubItemData(R.drawable.iv_movieposter1, "https://www.youtube.com/watch?v=0r85vZIzayg"))
        subItemDataList.add(SubItemData(R.drawable.iv_movieposter2, "https://www.youtube.com/watch?v=YIPz1JpaXDc"))
        subItemDataList.add(SubItemData(R.drawable.iv_movieposter3, "https://www.youtube.com/watch?v=LoRwHdN7H1g"))
        subItemDataList.add(SubItemData(R.drawable.iv_movieposter4, "https://www.youtube.com/watch?v=EkRuV-h6Bv0"))

        binding.rvMainOst.adapter = SubWebviewAdapter(this, subItemDataList)
        binding.rvMainOst.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.btnMainYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"))
            startActivity(intent)
        }
        binding.btnMainAsmr.setOnClickListener {
            val intent = Intent(this, AsmrActivity::class.java)
            startActivity(intent)
        }
    }
}