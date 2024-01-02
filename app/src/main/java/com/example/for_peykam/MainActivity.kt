@file:Suppress("DEPRECATION")

package com.example.for_peykam

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.for_peykam.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.Util

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var simpleExoPlayer: ExoPlayer
    private lateinit var binding: ActivityMainBinding

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private var pause:Boolean = false

    private lateinit var playBtn: Button
    private lateinit var pauseBtn: Button
    private lateinit var stopBtn: Button

    private lateinit var tv_pass: TextView
    private lateinit var tv_due: TextView

    private lateinit var seek_bar: SeekBar
    private val PICK_AUDIO = 2
    var AudioUri: Uri? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        playBtn = findViewById(R.id.playBtn)
        pauseBtn = findViewById(R.id.pauseBtn)
        stopBtn = findViewById(R.id.stopBtn)
        tv_pass = findViewById(R.id.tv_pass)
        tv_due = findViewById(R.id.tv_due)
        seek_bar = findViewById(R.id.seek_bar)

        binding.btnSave.setOnClickListener {
            Toast.makeText(this , "information saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnAdd.setOnClickListener {
            val audio = Intent()
            audio.type = "audio/*"  // you can find image, video, others
            audio.action = Intent.ACTION_OPEN_DOCUMENT
            startActivityForResult(Intent.createChooser(audio, "Select Audio"), PICK_AUDIO)
        }
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == PICK_AUDIO && resultCode == RESULT_OK) {
                // Audio is Picked in format of URI
                AudioUri = data!!.data
            }
        }
        // Start the media player
        playBtn.setOnClickListener{
            if(pause){
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
                Toast.makeText(this,"media playing", Toast.LENGTH_SHORT).show()
            }else{

                mediaPlayer = MediaPlayer.create(applicationContext,R.raw.amalya)
                mediaPlayer.start()
                Toast.makeText(this,"media playing", Toast.LENGTH_SHORT).show()

            }
            initializeSeekBar()
            playBtn.isEnabled = false
            pauseBtn.isEnabled = true
            stopBtn.isEnabled = true

            mediaPlayer.setOnCompletionListener {
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                Toast.makeText(this,"end", Toast.LENGTH_SHORT).show()
            }
        }

        // Pause the media player
        pauseBtn.setOnClickListener {
            if(mediaPlayer.isPlaying){
                mediaPlayer.pause()
                pause = true
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = true
                Toast.makeText(this,"media pause",Toast.LENGTH_SHORT).show()
            }
        }
        // Stop the media player
        stopBtn.setOnClickListener{
            if(mediaPlayer.isPlaying || pause.equals(true)){
                pause = false
                seek_bar.setProgress(0)
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                handler.removeCallbacks(runnable)

                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                tv_pass.text = ""
                tv_due.text = ""
                Toast.makeText(this,"media stop",Toast.LENGTH_SHORT).show()
            }
        }

        // Seek bar change listener
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

    }
    // Method to initialize seek bar and audio stats
    @SuppressLint("SetTextI18n")
    private fun initializeSeekBar() {
        seek_bar.max = mediaPlayer.seconds

        runnable = Runnable {
            seek_bar.progress = mediaPlayer.currentSeconds

            tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }
    // Creating an extension property to get the media player time duration in seconds
    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }
    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }


    private fun initializePlayer() {

        val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(STREAM_URL))

        val mediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

        simpleExoPlayer = ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()

        simpleExoPlayer.addMediaSource(mediaSource)

        simpleExoPlayer.playWhenReady = true
        binding.playerView.player = simpleExoPlayer
        binding.playerView.requestFocus()
    }

    private fun releasePlayer() {
        simpleExoPlayer.release()
    }

    public override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }

    companion object {
        const val STREAM_URL = "https://auh.com.tm/uploads/videoContent/online_petek.mp4"
        //const val RADIO_URL = "https://ulgamda.com/tracks/turkmenskie-pesni/6200-hajy-yazmammedow-amp-perhat-atayew-kim-tanayar.html"
    }
}



/*
val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(this)
val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
    .createMediaSource(MediaItem.fromUri(nativePlayer.HLS_STATIC_URL))
val mediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)
exoPlayer = ExoPlayer.Builder(this).setMediaSourceFactory(mediaSourceFactory).build()
exoPlayer.addMediaSource(mediaSource)

exoPlayer.playWhenReady = true
playerView.player = exoPlayer
playerView.requestFocus()*/

/*
exoPlayer = ExoPlayer.Builder(this).build().apply {
    playWhenReady = isPlayerPlaying
    seekTo(currentWindow, playbackPosition)
    setMediaItem(mediaItem, false)
    prepare()
}
playerView.player = exoPlayer

*/
