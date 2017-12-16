package org.alaeri.cityvibe.model

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * We can skip persistence at first but we want to make the refresh work from the start
 *
 */
class DataManagerImpl: DataManager {

    private val itunesAPI: ItunesAPI

    private val popularSongs = ArrayList<Song>()

    init {
        val retrofitItunes = Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        itunesAPI = retrofitItunes.create(ItunesAPI::class.java)
    }

    override fun refreshPopular(): Single<RefreshResults> =
        itunesAPI.search("Kanye")
                .map {
                    Log.d("DataManagerImpl","$it")
                    it.results.map {
                        Song(it.trackName, it.artistName, it.artworkUrl100, it.previewUrl)
                    } // We map to a format we can use
                }
                .map {
                    when(it) {
                        popularSongs -> RefreshResults.NoChange(it) as RefreshResults
                        else -> RefreshResults.NewResults(it) as RefreshResults //???
                    }

                }
                .doOnSuccess { it -> popularSongs.clear(); popularSongs.addAll(it.songs) }
                .onErrorReturn {
                    Log.e("DataManagerImpl","Exception: $it")
                    return@onErrorReturn  RefreshResults.NoConnection(songs = popularSongs)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

}

interface ItunesAPI {


    data class ItunesSongSearchResult(val count: Int, val results: List<ItunesSongSearchResultItem>)
    data class ItunesSongSearchResultItem(val trackName: String, val artistName: String,
                                          val previewUrl: String, val artworkUrl100: String)


    @GET("search?media=music&entity=song")
    fun search(@Query("term") action: String):
            Single<ItunesSongSearchResult>


}


interface ChartAPI {

    //GET https://api.music.apple.com/v1/catalog/{storefront}/charts?types={types}
}