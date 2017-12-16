package org.alaeri.cityvibe.cityvibe.home

import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.song_list_item.view.*
import org.alaeri.cityvibe.cityvibe.R
import org.alaeri.cityvibe.model.Song

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * This class displays data in the song list view
 */

class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val titleTextView : AppCompatTextView = itemView.titleTextView
    val artistTextView : AppCompatTextView = itemView.artistTextView
    val coverThumbImageView : AppCompatImageView = itemView.coverThumbImageView


}
/**
 * Simple songs adapter
 *
 */
class SongsAdapter(private val songs: List<Song>) : RecyclerView.Adapter<VH>() {

    private var layoutInflater : LayoutInflater? = null

    override fun onBindViewHolder(holder: VH, position: Int) {
        val song = songs[position]
        holder.artistTextView.text = song.artist
        holder.titleTextView.text = song.title
        Glide.with(holder.coverThumbImageView).load(song.coverUrl).into(holder.coverThumbImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        var li = layoutInflater
        if( li == null) {
            li = LayoutInflater.from(parent.context)!!
            layoutInflater = li
        }
        val view = li.inflate(R.layout.song_list_item, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

}