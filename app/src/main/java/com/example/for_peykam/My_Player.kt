@file:Suppress("DEPRECATION")

package com.example.for_peykam

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.for_peykam.databinding.ActivityMyPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView

@Suppress("DEPRECATION")
class My_Player : AppCompatActivity() {
    private lateinit var binding:ActivityMyPlayerBinding
    private var processbar: DefaultTimeBar? = null
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val playerView = findViewById<PlayerView>(R.id.player_view)
        processbar = findViewById(R.id.exo_progressBar)
        val btn_fullScreen = findViewById<ImageView>(R.id.imageViewFullScreen)
        val btn_lock = findViewById<ImageView>(R.id.imageViewLock)

        val simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()

        binding.playerView.player = simpleExoPlayer
        binding.playerView.keepScreenOn = true

        simpleExoPlayer.addListener(object: Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING){
                    processbar!!.visibility = View.VISIBLE
                }else if(playbackState == Player.STATE_READY){
                    processbar!!.visibility = View.GONE
                }
            }
        })

        val videoSource = Uri.parse("https://auh.com.tm/uploads/videoContent/online_petek.mp4")
        val mediaItem = MediaItem.fromUri(videoSource)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()

    }
}