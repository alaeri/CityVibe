package org.alaeri.cityvibe.model

import io.reactivex.Single

/**
 * Created by Emmanuel Requier on 16/12/2017.
 */

data class Song(val title: String, val artist: String, val coverUrl: String)
data class PlayableSong(val title: String, val artist: String, val coverUrl: String, val previewUrl: String)

val stubs = listOf(
        PlayableSong("HELLO WORLD", "Emmanuel", "https://www.fuse.tv/image/5a0489463a74bbf97d00001a/768/512/geazy-the-beatuful-and-damned-album-cover.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/Music6/v4/13/22/67/1322678b-e40d-fb4d-8d9b-3268fe03b000/mzaf_8818596367816221008.plus.aac.p.m4a"),
        PlayableSong("PLEASE WORK", "CHURCHES", "https://www.fuse.tv/image/5a3152b4acecb14a10000018/768/512/miguel-war-leisure-album-cover-full-size.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/Music6/v4/13/22/67/1322678b-e40d-fb4d-8d9b-3268fe03b000/mzaf_8818596367816221008.plus.aac.p.m4a"),
        PlayableSong("PLS CALL ME MAYBE", "MAYBE", "https://www.fuse.tv/image/5a3152b4acecb14a10000036/768/512/tyler-the-creator-flower-boy-album-cover-full-size.jpg", "https://audio-ssl.itunes.apple.com/apple-assets-us-std-000001/Music6/v4/13/22/67/1322678b-e40d-fb4d-8d9b-3268fe03b000/mzaf_8818596367816221008.plus.aac.p.m4a"))

sealed class RefreshResults(open val songs: List<Song>){

    class NoConnection(override val songs: List<Song>) : RefreshResults(songs)
    class NewResults(override val songs: List<Song>) : RefreshResults(songs)
    class NoChange(override val songs: List<Song>) : RefreshResults(songs)
}

interface DataManager {

    fun refreshPopular() : Single<RefreshResults>

    fun search(term: String): Single<RefreshResults>?
}