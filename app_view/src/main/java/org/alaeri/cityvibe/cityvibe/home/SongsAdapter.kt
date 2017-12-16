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

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * This class displays data in the song list view
 */
data class Stub(val title: String, val artist: String, val coverUrl: String)
val stubs = listOf(
        Stub("HELLO WORLD", "Emmanuel", "https://www.fuse.tv/image/5a0489463a74bbf97d00001a/768/512/geazy-the-beatuful-and-damned-album-cover.jpg"),
        Stub("PLEASE WORK","CHURCHES",  "https://www.fuse.tv/image/5a3152b4acecb14a10000018/768/512/miguel-war-leisure-album-cover-full-size.jpg"),
        Stub("PLS CALL ME MAYBE","MAYBE","https://www.fuse.tv/image/5a3152b4acecb14a10000036/768/512/tyler-the-creator-flower-boy-album-cover-full-size.jpg"))

class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val titleTextView : AppCompatTextView = itemView.titleTextView
    val artistTextView : AppCompatTextView = itemView.artistTextView
    val coverThumbImageView : AppCompatImageView = itemView.coverThumbImageView


}
/**
 * Simple songs adapter
 *
 */
class SongsAdapter : RecyclerView.Adapter<VH>() {

    private var layoutInflater : LayoutInflater? = null

    override fun onBindViewHolder(holder: VH, position: Int) {
        val stub = stubs[position]
        holder.artistTextView.text = stub.artist
        holder.titleTextView.text = stub.title
        Glide.with(holder.coverThumbImageView).load(stub.coverUrl).into(holder.coverThumbImageView)
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
        return stubs.size
    }

}