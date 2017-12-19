package org.alaeri.cityvibe.model

import android.content.Context
import io.reactivex.Single
import java.util.*

/**
 * Created by Emmanuel Requier on 16/12/2017.
 *
 *
 */

//A song to display
data class Song(val title: String, val artist: String, val coverUrl: String, val id: String)
//A song with a previewUrl (the RSS feed from itunes does not contain previewUrls)
data class PlayableSong(val title: String, val artist: String, val coverUrl: String, val previewUrl: String, val id: String)

enum class Provider{
    CACHE,
    NETWORK
}

sealed class Top {
    class NoResults(val message: String, val provider: Provider) : Top()
    class Results(val songs: List<Song>, val refreshDate: Date, val provider: Provider) : Top()
}

/**
 * This is the datalayer interface.
 *
 */
interface DataManager {

    val top : Top
    //refresh popular
    fun refreshPopular() : Single<Top>

    //search a list of songs
    fun search(term: String): Single<List<Song>>

    //find the previewUrl for a song so it can be played
    fun populateSong(id: String): Single<PlayableSong>

    var songQueue: List<Song>
    var positionInQueue : Int

    var context : Context?
}