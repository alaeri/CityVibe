package org.alaeri.cityvibe.model

import android.content.Context
import io.reactivex.Single

/**
 * Created by Emmanuel Requier on 16/12/2017.
 *
 *
 */

//A song to display
data class Song(val title: String, val artist: String, val coverUrl: String, val id: String)
//A song with a previewUrl (the RSS feed from itunes does not contain previewUrls)
data class PlayableSong(val title: String, val artist: String, val coverUrl: String, val previewUrl: String, val id: String)

//
sealed class RefreshResults(open val songs: List<Song>){
    class NoConnection(override val songs: List<Song>) : RefreshResults(songs)
    class NewResults(override val songs: List<Song>) : RefreshResults(songs)
    class NoChange(override val songs: List<Song>) : RefreshResults(songs)
}

/**
 * This is the datalayer interface.
 *
 */
interface DataManager {

    //refresh popular
    fun refreshPopular() : Single<RefreshResults>

    //search a list of songs
    fun search(term: String): Single<List<Song>>

    //find the previewUrl for a song so it can be played
    fun populateSong(id: String): Single<PlayableSong>

    //The popular songs
    val popularSongs: List<Song>

    var songQueue: List<Song>
    var positionInQueue : Int

    var context : Context?
}