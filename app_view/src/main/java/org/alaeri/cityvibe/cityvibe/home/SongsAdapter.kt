package org.alaeri.cityvibe.cityvibe.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.song_list_item.view.*
import org.alaeri.cityvibe.cityvibe.R

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * This class displays data in the song list view
 */
data class Stub(val title: String, val artist: String)
val stubs = listOf(
        Stub("HELLO WORLD", "Emmanuel"),
        Stub("PLEASE WORK","CHURCHES"),
        Stub("PLS CALL ME MAYBE","MAYBE"))

class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val titleTextView : TextView = itemView.titleTextView
    val artistTextView : TextView = itemView.artistTextView
    val coverThumbImageView : ImageView = itemView.coverThumbImageView


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