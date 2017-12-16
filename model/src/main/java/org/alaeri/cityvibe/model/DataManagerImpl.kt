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
import kotlin.collections.ArrayList

/**
 * Created by Emmanuel Requier on 16/12/2017.
 * We can skip persistence at first but we want to make the refresh work from the start
 *
 */
class DataManagerImpl: DataManager {

    private val itunesAPI: ItunesAPI
    private val chartsAPI: ChartAPI

    override val popularSongs = ArrayList<Song>()

    init {
        val retrofitItunes = Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        itunesAPI = retrofitItunes.create(ItunesAPI::class.java)

        val retrofitCharts = Retrofit.Builder()
                .baseUrl("https://rss.itunes.apple.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        chartsAPI = retrofitCharts.create(ChartAPI::class.java)

    }

    override fun refreshPopular(): Single<RefreshResults> =
        chartsAPI.topChartsUs()
                .map {
                    Log.d("DataManagerImpl","$it")
                    it.feed.results.map {
                        Song(it.name, it.artistName, it.artworkUrl100)
                    } // We map to a format we can use
                }
                .map {
                    when {
                        it.isSame(popularSongs) -> RefreshResults.NoChange(it)
                        else -> RefreshResults.NewResults(it)
                    }
                }
                .doOnSuccess { it -> popularSongs.clear(); popularSongs.addAll(it.songs) }
                .onErrorReturn {
                    Log.e("DataManagerImpl","Exception: $it")
                    return@onErrorReturn  RefreshResults.NoConnection(songs = popularSongs)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    private fun <T> List<T>.isSame(o: List<T>): Boolean  {
        val temp = ArrayList(this)
        temp.retainAll(o)
        Log.d("DataManagerImpl","size: ${temp.size} / ${this.size}")
        return temp.size == this.size
    }

    override fun search(term: String) =
            itunesAPI.search(term)
                    .map {
                        Log.d("DataManagerImpl","$it")
                        it.results.map {
                            Song(it.trackName, it.artistName, it.artworkUrl100)
                        } // We map to a format we can use
                    }
                    .doOnError {
                        Log.e("DataManagerImpl", "error searching $it", it.cause)
                    }
                    .onErrorReturn { ArrayList() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

}

interface ItunesAPI {


    data class ItunesSongSearchResult(val count: Int, val results: List<ItunesSongSearchResultItem>)
    data class ItunesSongSearchResultItem(val trackName: String, val artistName: String,
                                          val previewUrl: String, val artworkUrl100: String)


    @GET("search?media=music&entity=song")
    fun search(@Query("term") term: String):
            Single<ItunesSongSearchResult>

}


interface ChartAPI {

    data class FeedResponse(val feed: Feed)
    data class Feed(val results: List<RSSResultItem>)
    data class RSSResultItem(val name: String, val artistName: String, val artworkUrl100: String)

    @GET("api/v1/us/apple-music/hot-tracks/all/100/explicit.json")
    fun topChartsUs() : Single<FeedResponse>

}