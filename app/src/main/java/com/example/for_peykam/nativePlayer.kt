@file:Suppress("DEPRECATION")

package com.example.for_peykam

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util

class
nativePlayer : AppCompatActivity() {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var playerView: StyledPlayerView
    private lateinit var exoProgress: DefaultTimeBar
    private var previewFrameLayout: FrameLayout? = null
    private var previewImage: ImageView? = null

    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var isFullscreen = false
    private var isPlayerPlaying = true
    private val mediaItem = MediaItem.Builder().setUri(HLS_STATIC_URL).setMimeType(MimeTypes.APPLICATION_M3U8).build()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_player)

        playerView = findViewById(R.id.player_view)
        exoProgress = playerView.findViewById(R.id.exo_progress)
        previewFrameLayout = findViewById(R.id.preview_frame_layout)
        previewImage = findViewById(R.id.preview_image)

        dataSourceFactory = DefaultDataSource.Factory(this)


    }

    private fun initPlayer() {
        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(HLS_STATIC_URL))

        val mediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

        exoPlayer = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        exoPlayer.addMediaSource(mediaSource)
//        exoPlayer.seekTo(currentWindow, playbackPosition)

        exoPlayer.playWhenReady = true
        playerView.player = exoPlayer
        playerView.requestFocus()

        exoProgress.addListener(object : TimeBar.OnScrubListener {
            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                previewFrameLayout?.visibility = View.VISIBLE
                previewFrameLayout?.x =
                    updatePreviewX(position.toInt(), exoPlayer.duration.toInt()).toFloat()

                val drawable = previewImage?.drawable
                var glideOptions = RequestOptions().dontAnimate().skipMemoryCache(false)
                if(drawable != null) {
                    glideOptions = glideOptions.placeholder(drawable)
                }

                Glide.with(previewImage!!).asBitmap()
                    .apply(glideOptions)
                    .load(THUMBNAIL_MOSAIQUE)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .transform(GlideThumbnailTransformation(position))
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(previewImage!!)
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                previewFrameLayout?.visibility = View.INVISIBLE
            }

            override fun onScrubStart(timeBar: TimeBar, position: Long) {}
        })

    }

    private fun updatePreviewX(progress: Int, max: Int): Int {
        if(max == 0) return 0

        val parent = previewFrameLayout?.parent as ViewGroup
        val layoutParams = previewFrameLayout?.layoutParams as ViewGroup.MarginLayoutParams
        val offset = progress.toFloat() / max
        val minimumX: Int? = previewFrameLayout?.left
        val maximumX = (parent.width - parent.paddingRight - layoutParams.rightMargin)

        val previewPaddingRadius: Int = resources.getDimensionPixelSize(R.dimen.scrubber_dragged_size).div(2)
        val previewLeftX = (exoProgress as View).left.toFloat()
        val previewRightX = (exoProgress as View).right.toFloat()
        val previewSeekBarStartX: Float = previewLeftX + previewPaddingRadius
        val previewSeekBarEndX: Float = previewRightX - previewPaddingRadius
        val currentX = (previewSeekBarStartX + (previewSeekBarEndX - previewSeekBarStartX) * offset)
        val startX: Float = currentX - previewFrameLayout?.width!! / 2f
        val endX: Float = startX + previewFrameLayout?.width!!

        // Clamp the moves
        return if(startX >= minimumX!! && endX <= maximumX) {
            startX.toInt()
        } else if(startX < minimumX) {
            minimumX
        } else {
            maximumX - previewFrameLayout?.width!!
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onResume() {
        super.onResume()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initPlayer()
            playerView.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerView.onPause()
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            playerView.onPause()
            releasePlayer()
        }
    }

    companion object {
        const val HLS_STATIC_URL =
            "https://auh.com.tm/uploads/videoContent/online_petek.mp4"
        const val THUMBNAIL_MOSAIQUE =
            "https://api.100haryt.com.tm/img/banners/927690.bqu.webp"
        const val STATE_RESUME_WINDOW = "resumeWindow"
        const val STATE_RESUME_POSITION = "resumePosition"
        const val STATE_PLAYER_FULLSCREEN = "playerFullscreen"
        const val STATE_PLAYER_PLAYING = "playerOnPlay"
    }
}