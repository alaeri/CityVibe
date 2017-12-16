package org.alaeri.cityvibe.model

import android.annotation.SuppressLint
import android.os.Parcelable
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize

/**
 * Created by Emmanuel Requier on 16/12/2017.
 *
 *
 */
@SuppressLint("ParcelCreator") //Lint is wrong on this experimental feature
@Parcelize
data class Song(val title: String, val artist: String, val coverUrl: String, val id: String) : Parcelable

@SuppressLint("ParcelCreator") //Lint is wrong on this experimental feature
@Parcelize
data class PlayableSong(val title: String, val artist: String, val coverUrl: String, val previewUrl: String, val id: String) : Parcelable

sealed class RefreshResults(open val songs: List<Song>){

    class NoConnection(override val songs: List<Song>) : RefreshResults(songs)
    class NewResults(override val songs: List<Song>) : RefreshResults(songs)
    class NoChange(override val songs: List<Song>) : RefreshResults(songs)
}

interface DataManager {

    fun refreshPopular() : Single<RefreshResults>

    fun search(term: String): Single<List<Song>>

    val popularSongs: List<Song>

    fun populateSong(id: String): Single<PlayableSong>
}