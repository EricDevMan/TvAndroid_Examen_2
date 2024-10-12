package com.example.tvandroidprofe

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var channelList: MutableList<Channel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChannelAdapter
    private val sharedPrefsKey = "CHANNEL_LIST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.playerView)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Load saved channels or initialize with default channels
        channelList = loadChannels()

        // If there are channels, play the first one
        if (channelList.isNotEmpty()) {
            playChannel(channelList[0].url)
        }

        // Initialize RecyclerView adapter
        adapter = ChannelAdapter(channelList, onItemClick = { channel ->
            playChannel(channel.url)
        }, onItemEdit = { channel, position ->
            showAddEditChannelDialog(channel) { updatedChannel ->
                channelList[position] = updatedChannel
                adapter.notifyItemChanged(position)
                saveChannels()
            }
        }, onItemDelete = { position ->
            channelList.removeAt(position)
            adapter.notifyItemRemoved(position)
            saveChannels()
        })
        recyclerView.adapter = adapter

        // Button to add a new channel
        findViewById<View>(R.id.addChannelButton).setOnClickListener {
            showAddEditChannelDialog(null) { newChannel ->
                channelList.add(newChannel)
                adapter.notifyItemInserted(channelList.size - 1)
                saveChannels()
            }
        }
    }

    private fun playChannel(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    // Save channels to SharedPreferences
    private fun saveChannels() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        val jsonArray = JSONArray()
        for (channel in channelList) {
            val jsonObject = JSONObject()
            jsonObject.put("name", channel.name)
            jsonObject.put("url", channel.url)
            jsonObject.put("logo", channel.logo)
            jsonArray.put(jsonObject)
        }

        editor.putString(sharedPrefsKey, jsonArray.toString())
        editor.apply()
    }

    // Load channels from SharedPreferences
    private fun loadChannels(): MutableList<Channel> {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val savedChannels = prefs.getString(sharedPrefsKey, null)

        val channels = mutableListOf<Channel>()
        if (savedChannels != null) {
            val jsonArray = JSONArray(savedChannels)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val url = jsonObject.getString("url")
                val logo = jsonObject.getString("logo")
                channels.add(Channel(name, url, logo))
            }
        } else {
            // Default channels
            channels.add(Channel("Canal de las estrellas", "https://channel01-onlymex.akamaized.net/hls/live/2022749/event01/index.m3u8", "https://th.bing.com/th/id/R.f287f024fb98505160209ddf885b91c5?rik=UUzrGFJnNKm6tw&riu=http%3a%2f%2fimages.mi.tv%2fchannels%2fcl_canal-de-las-estrellas_m.png&ehk=WeR4N2%2b41FbWUl4DKkuqqqB0Kr1%2f6GduORX6ahMXOKA%3d&risl=&pid=ImgRaw&r=0"))
            channels.add(Channel("ADN 40", "https://mdstrm.com/live-stream-playlist/60b578b060947317de7b57ac.m3u8", "https://th.bing.com/th/id/R.d46444904658ce1e4f895c0338ce27c1?rik=wQLUrCQ6fdNFzQ&riu=http%3a%2f%2fdirectostv.teleame.com%2fwp-content%2fuploads%2f2018%2f02%2fADN-40-en-vivo-Online.png&ehk=%2fhGu0g2u9i5yfict%2fDb8xgEzRBujBfIgEZ7itohFsQ8%3d&risl=&pid=ImgRaw&r=0"))
        }
        return channels
    }

    private fun showAddEditChannelDialog(channel: Channel?, onSave: (Channel) -> Unit) {
        val dialog = AddEditChannelDialog(channel, onSave)
        dialog.show(supportFragmentManager, "AddEditChannelDialog")
    }

    override fun onStop() {
        super.onStop()
        player.release()
        saveChannels()
    }
}
