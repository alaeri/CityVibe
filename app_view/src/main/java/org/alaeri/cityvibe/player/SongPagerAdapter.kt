package org.alaeri.cityvibe.player

import android.graphics.drawable.Drawable
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
import kotlinx.android.synthetic.main.page_song.view.*
import org.alaeri.cityvibe.R
import org.alaeri.cityvibe.model.Song

class SongPagerAdapter(val activity: AppCompatActivity) : PagerAdapter() {

    var songs : List<Song>? = null

    private var layoutInflater : LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = songs?.size ?: 0

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

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        Glide.with(view.context).clear(view.coverLargeImageView)
    }

    private fun populatePage(position: Int, view: View) {
        val song = songs?.get(position)
        song?.apply {
            Log.d("PlayerActivity", "Display song: $song")
            Glide.with(view.context).load(song.coverUrl).listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    activity.supportStartPostponedEnterTransition()
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    activity.supportStartPostponedEnterTransition()
                    return false
                }
            }).into(view.coverLargeImageView)
            ViewCompat.setTransitionName(view.coverLargeImageView, song.coverUrl)
            view.titleTextView.text = song.title
            view.artistTextView.text = song.artist
        }

    }
}