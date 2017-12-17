package org.alaeri.cityvibe.player

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_player.*
import kotlinx.android.synthetic.main.page_song.view.*
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

        viewPager.adapter = SongPagerAdapter(songs as ArrayList<Song>)
        viewPager.currentItem = songPosition
//        viewPager.setPageTransformer(true, DepthPageTransformer())

        val dataManager = (application  as CityVibeApp).dataManager

        val sub = dataManager.populateSong(song.id).subscribe { it ->

            Log.d("PlayerActivity", "Playable song: $it")
            mp.reset()
            mp.setDataSource(this, Uri.parse(it.previewUrl))
            mp.prepare()
            mp.start()

        }
        compositeDispo.add(sub)

        quitButton.setOnClickListener{ finish() } //Add transition to bottom
        mp.setOnCompletionListener { finish() }

//        supportStartPostponedEnterTransition()
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

    inner class SongPagerAdapter(private val songs: List<Song>) : PagerAdapter() {

        private var layoutInflater : LayoutInflater? = null

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int = songs.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var li = layoutInflater
            if( li == null) {
                li = LayoutInflater.from(container.context)!!
                layoutInflater = li
            }
            val  view = li.inflate(R.layout.page_song, container, false )
            container.addView(view)
            populatePage(position, view)
            return view
        }

        private fun populatePage(position: Int, view: View) {
            val song = songs[position]
            Log.d("PlayerActivity", "Display song: $song")
            Glide.with(view.context).load(song.coverUrl).listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    supportStartPostponedEnterTransition()
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    supportStartPostponedEnterTransition()
                    return false
                }
            }).into(view.coverLargeImageView)
            ViewCompat.setTransitionName(view.coverLargeImageView, song.coverUrl)
            view.titleTextView.text = song.title
            view.artistTextView.text = song.artist
            supportStartPostponedEnterTransition()
        }



        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//            Glide.with(container.context).clear()
        }
    }
}