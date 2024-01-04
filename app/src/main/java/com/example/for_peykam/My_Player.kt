@file:Suppress("DEPRECATION")

package com.example.for_peykam

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.for_peykam.databinding.ActivityMyPlayerBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource


@Suppress("DEPRECATION")
class My_Player : AppCompatActivity() {
    private lateinit var binding:ActivityMyPlayerBinding
    private var processbar: DefaultTimeBar? = null
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    var playerView: PlayerView? = null
    var fullScreen: ImageView? = null
    var isFullScreen = false
    var player: SimpleExoPlayer? = null
    var progressBar: ProgressBar? = null
    private val isShowingTrackSelectionDialog = false
    private var trackSelector: DefaultTrackSelector? = null

    var speed = arrayOf("0.25x", "0.5x", "Normal", "1.5x", "2x")
    var live_url = "https://auh.com.tm/uploads/videoContent/online_petek.mp4"

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerView = findViewById(R.id.playerView)
        processbar = findViewById(R.id.exo_progressBar)
        fullScreen = findViewById(R.id.imageViewFullScreen)
        val btn_lock = findViewById<ImageView>(R.id.imageViewLock)

        trackSelector = DefaultTrackSelector(this)
        player = SimpleExoPlayer.Builder(this).setTrackSelector(trackSelector!!).build()

        playerView!!.player = player;

        dataSourceFactory = DefaultDataSource.Factory(this)

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(nativePlayer.HLS_STATIC_URL))

        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
        exoPlayer.addMediaSource(mediaSource)

        player!!.setMediaSource(mediaSource);
        player!!.prepare();
        player!!.setPlayWhenReady(true);


        fullScreen!!.setOnClickListener {
            if (isFullScreen) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                if (supportActionBar != null) {
                    supportActionBar!!.show()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                val params = playerView!!.layoutParams as ConstraintLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height =
                    (200 * applicationContext.resources.displayMetrics.density).toInt()
                playerView!!.layoutParams = params

                //                    Toast.makeText(Details.this, "We are Now going back to normal mode.", Toast.LENGTH_SHORT).show();
                isFullScreen = false
            } else {
                window.decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                if (supportActionBar != null) {
                    supportActionBar!!.hide()
                }
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                val params = playerView!!.layoutParams as ConstraintLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                playerView!!.layoutParams = params

                //                    Toast.makeText(Details.this, "We are going to FullScreen Mode.", Toast.LENGTH_SHORT).show();
                isFullScreen = true
            }
        }
    }
}