package org.alaeri.cityvibe.player

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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

    private val compositeDispo = CompositeDisposable()
    private val mp = MediaPlayer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        supportPostponeEnterTransition()

        val songPosition = intent.extras[HomeActivity.KEY_EXTRA_SELECTED_SONG_POSITION] as Int
        val songs = intent.extras[HomeActivity.KEY_EXTRA_SONGS] as ArrayList<*>
        val song = songs[songPosition] as Song

        Glide.with(this).load(song.coverUrl).listener(object: RequestListener<Drawable>{
            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                supportStartPostponedEnterTransition()
                return false
            }

            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                supportStartPostponedEnterTransition()
                return false
            }
        }).into(coverLargeImageView)
        ViewCompat.setTransitionName(coverLargeImageView, song.coverUrl)
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

        quitButton.setOnClickListener{ finish() } //Add transition to bottom
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