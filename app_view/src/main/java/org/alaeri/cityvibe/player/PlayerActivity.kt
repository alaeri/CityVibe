package org.alaeri.cityvibe.player

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_player.*
import org.alaeri.cityvibe.R
import org.alaeri.cityvibe.cityvibe.CityVibeApp
import org.alaeri.cityvibe.home.HomeActivity
import org.alaeri.cityvibe.model.Song

/**
 * Created by Emmanuel Requier on 16/12/2017.
 */
class PlayerActivity: AppCompatActivity() {

    val compositeDispo = CompositeDisposable()
    val mp = MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val song = intent.extras[HomeActivity.KEY_EXTRA_SONG] as Song

        Glide.with(this).load(song.coverUrl).into(coverLargeImageView)
        titleTextView.text = song.title
        artistTextView.text = song.artist

        val dataManager = (application  as CityVibeApp).dataManager

        val sub = dataManager.populateSong(song.id).subscribe { it ->

            Log.d("PlayerActivity", "Playable song: $it")
            mp.reset()
            mp.setDataSource(this, Uri.parse(it.previewUrl))
            mp.prepare()
            mp.start()

        }

        mp.setOnCompletionListener { finish() }
        compositeDispo.add(sub)
    }

    override fun onPause() {
        super.onPause()
        clean()
    }

    private fun clean() {
        compositeDispo.clear()
        mp.reset()
        mp.release()
    }
}