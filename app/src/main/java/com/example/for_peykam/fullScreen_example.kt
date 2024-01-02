package com.example.for_peykam

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

@Suppress("DEPRECATION")
class fullScreen_example() : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var playerView: StyledPlayerView
    private lateinit var mainFrameLayout: FrameLayout

    private var exoFullScreenBtn: FrameLayout? = null
    private var exoFullScreenIcon: ImageView? = null

    private var fullscreenDialog: Dialog? = null
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_example)

        exoFullScreenBtn = findViewById(R.id.preview_frame_layout)
        exoFullScreenIcon = findViewById(R.id.exo_fullscreen_icon)
        playerView = findViewById(R.id.player_view)
        mainFrameLayout = findViewById(R.id.main_media_frame)


        initFullScreenDialog()
        initFullScreenButton()


    }

    private fun initPlayer() {

        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource
            .Factory(this)

        val mediaSource = ProgressiveMediaSource
            .Factory(mediaDataSourceFactory)
            .createMediaSource(
                MediaItem
                    .fromUri(HLS_STATIC_URL)
            )

        val mediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

        exoPlayer = ExoPlayer
            .Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        exoPlayer.addMediaSource(mediaSource)

        exoPlayer.playWhenReady = true
        playerView.player = exoPlayer
        playerView.requestFocus()


        if (isFullscreen) {
            openFullscreenDialog()
        }
    }

    private fun releasePlayer() {
        isPlayerPlaying = exoPlayer.playWhenReady
        playbackPosition = exoPlayer.currentPosition
        currentWindow = exoPlayer.currentMediaItemIndex
        exoPlayer.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_RESUME_WINDOW, exoPlayer.currentMediaItemIndex)
        outState.putLong(STATE_RESUME_POSITION, exoPlayer.currentPosition)
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, isFullscreen)
        outState.putBoolean(STATE_PLAYER_PLAYING, isPlayerPlaying)
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerView.onPause()
            releasePlayer()
        }
    }

    // FULLSCREEN PART

    private fun initFullScreenDialog() {
        fullscreenDialog =
            object : Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                @Deprecated("Deprecated in Java")
                override fun onBackPressed() {
                    if (isFullscreen) closeFullscreenDialog()
                    super.onBackPressed()
                }
            }
    }


    private fun initFullScreenButton() {
        exoFullScreenBtn?.setOnClickListener {
            if (!isFullscreen) {
                openFullscreenDialog()
            } else {
                closeFullscreenDialog()
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun openFullscreenDialog() {
        exoFullScreenIcon?.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_shrink)
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        (playerView.parent as ViewGroup).removeView(playerView)
        fullscreenDialog?.addContentView(
            playerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        isFullscreen = true
        fullscreenDialog?.show()
    }

    private fun closeFullscreenDialog() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        (playerView.parent as ViewGroup).removeView(playerView)
        mainFrameLayout.addView(playerView)
        exoFullScreenIcon?.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand)
        )
        isFullscreen = false
        fullscreenDialog?.dismiss()
    }


    companion object {
        const val HLS_STATIC_URL =
            "https://auh.com.tm/uploads/videoContent/online_petek.mp4"
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }

}